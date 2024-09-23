package br.com.agilizeware.payment.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.BrandEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.StatusPaymentEnum;
import br.com.agilizeware.enums.TypeCardEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.Payment;
import br.com.agilizeware.payment.ServiceAuthenticationFilter;
import br.com.agilizeware.payment.repository.PaymentDaoImpl;
import br.com.agilizeware.payment.util.RegexCardValidator;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.util.Util;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.CieloEcommerce;
import cieloecommerce.sdk.ecommerce.Environment;
import cieloecommerce.sdk.ecommerce.Payment.Type;
import cieloecommerce.sdk.ecommerce.Sale;
import cieloecommerce.sdk.ecommerce.request.CieloRequestException;

@Service
@RestController
@RequestMapping("/payment")
public class PaymentServiceRest extends ServiceRestEntityAb<Payment, Long> {
	
	private static final String URL_PAYMENT_RETURN = "/app/payment/#/agilize/payment/capture/debit/";
	
	@Autowired
	private PaymentDaoImpl paymentDaoIf;
	
	@Override
	protected DaoAB<Payment, Long> definirDao() {
		return paymentDaoIf;
	}
	
	/**
	 * Operação que irá gerar uma transação automática - SIMPLES - de crédito na Ciello.
	 * O meio utilizado é o Cartão de Crédito
	 * @param record
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/automPayment/simple/creditCard", method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Payment> automCreditPayment(@RequestParam(name="idUser", required=false) Long idUser, @RequestBody Payment record) {
		
		RestResultDto<Payment> result = new RestResultDto<Payment>();
		
		//validando o parâmetro recebido
		List<AgilizeException> errors = validateInitPayment(record, 1);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
			return result;
		}
		
		if(Util.isNotNull(idUser)) {
			record.setIdUserCreate(idUser);
		}
		Application app = null;
		try {
			 app = findApplicationByName(record);
		}catch(AgilizeException age) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(age));
			return result;
		}
		
		//fazendo verificações pré-pagamento
		if(Util.isNotNull(app.getUrlValidationPrePayment())) {
			Object prePay = Util.accessRestService(app.getUrlValidationPrePayment()+"/"+record.getMerchantOrderId(), 5, null, 
					RestResultDto.class, app.getPassword(), null);
			if(!Util.isNotNull(prePay) || (!(Boolean)prePay)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
						HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ERROR_VALIDATIONS_PRE_PAYMENT)));
				return result;
			}
		}
		
		//Criando a transação na Ciello
		Merchant merchant = new Merchant(app.getMerchantId(), app.getMerchantKey());
		Environment env;
		if(Util.isNotNull(app.getFlgEnvironmentPayProd()) && app.getFlgEnvironmentPayProd()) {
			env = Environment.PRODUCTION;
		}
		else {
			env = Environment.SANDBOX;
		}
		//Chamada a API da Ciello
		CieloEcommerce ce = new CieloEcommerce(merchant, env);
		try {
			Sale saleCaptured = null;
			record.getSale().setMerchantOrderId(record.getMerchantOrderId());
			record.getSale().getPayment().setType(Type.CreditCard);
			if(!Util.isNotNull(record.getSale().getPayment().getInstallments())) {
				record.getSale().getPayment().setInstallments(1);
			}
			record.getSale().getPayment().setReturnUrl(app.getPaymentUrlReturn());
			Sale sale = ce.createSale(record.getSale());
			Integer status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
			if(StatusPaymentEnum.NOT_FINISHED.getId().equals(status)) {
				//Tentando novamente
				sale = ce.createSale(record.getSale());
				status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
			}
			
			//verificando se o status retornado é o esperado
			if(!(StatusPaymentEnum.AUTHORIZED.getId().equals(status) || 
					StatusPaymentEnum.CONFIRMED_PAYMENT.getId().equals(status))) {
				//a operação tem que ser cancelada
				return cancelPagmto(status, ce, sale.getPayment().getPaymentId());
			}
			
			//caso o retorno da transação seja apenas authorizado, é ncessário capturar a transação de modo que a mesma possa ser efetivada.
			//TODO: Verificar se o correto é || ou &&
			if(StatusPaymentEnum.AUTHORIZED.getId().equals(status) || sale.getPayment().getCapture()) {
				saleCaptured = ce.captureSale(sale.getPayment().getPaymentId());
				//se o status nao for confirmed_payment, gerar erro
				status = saleCaptured.getStatus() != null ? saleCaptured.getStatus() : saleCaptured.getPayment().getStatus();
				if(!StatusPaymentEnum.CONFIRMED_PAYMENT.getId().equals(status)) {
					//a operação tem que ser cancelada
					return cancelPagmto(status, ce, sale.getPayment().getPaymentId());
				}
			}
			
			record = convertSaleToPayment(record, sale, status, TypeCardEnum.CREDIT, app.getId());
			
			try {
				paymentDaoIf.save(record);
			}
			catch(Exception ex) {
				
				log.error("Erro ao salvar pagamento: "+ex.getMessage(), ex);
				
				//caso a informação não possa ser gravada,  a operação tem que ser cancelada
				result.setSuccess(false);
				try {
					
					result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
							HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_BD, ex, ex.getMessage())));
					ce.cancelSale(sale.getPayment().getPaymentId());
				
				}catch(JsonProcessingException jpe) {
					log.error("Erro ao cancelar pagamento após dar erro em salvar pagamento: "+jpe.getMessage(), jpe);
					throw new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_JSON);
				}catch(IOException | CieloRequestException e) {
					//Abafada
					log.error("Erro ao cancelar pagamento após dar erro em salvar pagamento: "+e.getMessage(), e);
				}
				return result;
			}
			
			record.setSale(sale);
			
			//fazendo processamento após-pagamento
			if(Util.isNotNull(app.getUrlProcessPosPayment())) {
				try {
					Util.accessRestService(app.getUrlProcessPosPayment()+"/"+record.getMerchantOrderId(), 5, null, 
							RestResultDto.class, app.getPassword(), null);
				}
				catch(Throwable th) {
					try {	
						ce.cancelSale(sale.getPayment().getPaymentId());
					}catch(IOException | CieloRequestException e) {
						//Abafada
						log.error("Erro ao realizar processamento pós pagamento: "+e.getMessage(), e);
					}
					throw th;
				}
			}
		}
		catch(IOException | CieloRequestException e) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_GATEWAY.ordinal(), ErrorCodeEnum.ERROR_CONNECT_CIELLO_INIT_PAYMENT, e, e.getMessage())));
			return result;
		}
		
		result.setSuccess(true);
		result.setData(record);
		return result;
	}
	
	/**
	 * Operação que irá cancelar uma transação na Ciello.
	 * O meio utilizado é o Cartão de Crédito
	 * @param record
	 * @return
	 */
	@RequestMapping(value="/cancel", method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Payment> cancelPayment(@RequestBody Payment record) {
		
		RestResultDto<Payment> result = new RestResultDto<Payment>();
		
		//validando o parâmetro recebido
		List<AgilizeException> errors = validateInitPayment(record, 3);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
			return result;
		}
		
		Application app = null;
		try {
			 app = findApplicationByName(record);
		}catch(AgilizeException age) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(age));
			return result;
		}
		
		//Cancelando a transação na Ciello
		Merchant merchant = new Merchant(app.getMerchantId(), app.getMerchantKey());
		Environment env;
		
		if(Util.isNotNull(app.getFlgEnvironmentPayProd()) && app.getFlgEnvironmentPayProd()) {
			env = Environment.PRODUCTION;
		}
		else {
			env = Environment.SANDBOX;
		}
		//Chamada a API da Ciello
		CieloEcommerce ce = new CieloEcommerce(merchant, env);
		try {
			Sale sale = ce.cancelSale(record.getPaymentId());
			Integer status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
			paymentDaoIf.updatePayment(record.getPaymentId(), StatusPaymentEnum.findByCode(status), record.getIdUserCreate());
			
		} catch(IOException | CieloRequestException e) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_GATEWAY.ordinal(), ErrorCodeEnum.ERROR_CONNECT_CIELLO_CANCEL_PAYMENT, e, e.getMessage())));
			return result;
		}
		
		result.setSuccess(true);
		result.setData(record);
		return result;
	}
	
	
	/**
	 * Operação que irá gerar uma transação - SIMPLES - de débito na Ciello.
	 * O meio utilizado é o Cartão de Débito
	 * @param record
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/init/simple/debitCard", method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Payment> initDebitPayment(@RequestParam(name="idUser") Long idUser, @RequestBody Payment record) {
		
		RestResultDto<Payment> result = new RestResultDto<Payment>();
		
		//validando o parâmetro recebido
		List<AgilizeException> errors = validateInitPayment(record, 2);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
			return result;
		}
		
		Application app = null;
		try {
			 app = findApplicationByName(record);
		}catch(AgilizeException age) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(age));
			return result;
		}
		
		//fazendo verificações pré-pagamento
		if(Util.isNotNull(app.getUrlValidationPrePayment())) {
			Object prePay = Util.accessRestService(app.getUrlValidationPrePayment()+"/"+record.getMerchantOrderId(), 5, null, 
					RestResultDto.class, app.getPassword(), null);
			if(!Util.isNotNull(prePay) || (!(Boolean)prePay)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
						HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ERROR_VALIDATIONS_PRE_PAYMENT)));
				return result;
			}
		}
		
		//Criando a transação na Ciello
		Merchant merchant = new Merchant(app.getMerchantId(), app.getMerchantKey());
		Environment env;
		if(Util.isNotNull(app.getFlgEnvironmentPayProd()) && app.getFlgEnvironmentPayProd()) {
			env = Environment.PRODUCTION;
		}
		else {
			env = Environment.SANDBOX;
		}
		//Chamada a API da Ciello
		CieloEcommerce ce = new CieloEcommerce(merchant, env);
		try {
			record.getSale().setMerchantOrderId(record.getMerchantOrderId());
			record.getSale().getPayment().setType(Type.DebitCard);
			record.getSale().getPayment().setReturnUrl(record.getSale().getPayment().getReturnUrl() +
					URL_PAYMENT_RETURN + record.getSale().getPayment().getAmount() + "/" + 
					record.getSale().getMerchantOrderId());
			if(!Util.isNotNull(record.getSale().getPayment().getInstallments())) {
				record.getSale().getPayment().setInstallments(1);
			}
			record.getSale().getPayment().setDebitCard(record.getSale().getPayment().getCreditCard());
			record.getSale().getPayment().setReturnUrl(app.getPaymentUrlReturn());
			Sale sale = ce.createSale(record.getSale());
			Integer status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
			
			//verificando se o status retornado é o esperado
			if(!(StatusPaymentEnum.NOT_FINISHED.getId().equals(status) || 
					StatusPaymentEnum.AUTHORIZED.getId().equals(status) || 
					StatusPaymentEnum.CONFIRMED_PAYMENT.getId().equals(status))) {
				return cancelPagmto(status, ce, sale.getPayment().getPaymentId());
			}
			
			record = convertSaleToPayment(record, sale, status, TypeCardEnum.DEBIT, app.getId());

			try {
				paymentDaoIf.save(record);
			}
			catch(Exception ex) {
				//caso a informação não possa ser gravada,  a operação tem que ser cancelada
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
							HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_BD, ex, ex.getMessage())));
				try {	
					ce.cancelSale(sale.getPayment().getPaymentId());
				}catch(IOException | CieloRequestException e) {
					//Abafada
					log.error("Erro ao salvar pagamento: "+e.getMessage(), e);
				}
				return result;
			}
			
			record.setSale(sale);
			
			//fazendo processamento após-pagamento
			if(Util.isNotNull(app.getUrlProcessPosPayment())) {
				try {
					Util.accessRestService(app.getUrlProcessPosPayment()+"/"+record.getMerchantOrderId(), 5, null, 
							RestResultDto.class, app.getPassword(), null);
				}
				catch(Throwable th) {
					try {	
						ce.cancelSale(sale.getPayment().getPaymentId());
					}catch(IOException | CieloRequestException e) {
						//Abafada
						log.error("Erro ao realizar processamento pós pagamento: "+e.getMessage(), e);
					}
					throw th;
				}
			}
		}
		catch(IOException | CieloRequestException e) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_GATEWAY.ordinal(), ErrorCodeEnum.ERROR_CONNECT_CIELLO_INIT_PAYMENT, e, e.getMessage())));
			return result;
		}
		
		result.setSuccess(true);
		result.setData(record);
		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value="/finalize/simple/debitCard", method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Payment> finalizeDebitPayment(@RequestParam(name="idUser", required=true) Long idUser, @RequestBody Payment record) {
		
		RestResultDto<Payment> result = new RestResultDto<Payment>();
		
		//validando o parâmetro recebido
		List<AgilizeException> errors = validateInitPayment(record, 4);
		if(Util.isListNotNull(errors)) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
			return result;
		}
		
		Application app = null;
		try {
			 app = findApplicationByName(record);
		}catch(AgilizeException age) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(age));
			return result;
		}
		
		Merchant merchant = new Merchant(app.getMerchantId(), app.getMerchantKey());
		Environment env;
		
		if(Util.isNotNull(app.getFlgEnvironmentPayProd()) && app.getFlgEnvironmentPayProd()) {
			env = Environment.PRODUCTION;
		}
		else {
			env = Environment.SANDBOX;
		}
		//Chamada a API da Ciello
		CieloEcommerce ce = new CieloEcommerce(merchant, env);
		try {
			
			Payment paym = paymentDaoIf.findByMerchantOrderId(record.getMerchantOrderId());
			Sale sale = ce.querySale(paym.getPaymentId());
			Integer status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
					
			//Transitando a venda
			if(StatusPaymentEnum.NOT_FINISHED.getId().equals(status)) {
				sale = ce.createSale(sale);
				status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
			}
			
			if(!(StatusPaymentEnum.CONFIRMED_PAYMENT.getId().equals(status) ||
					StatusPaymentEnum.AUTHORIZED.getId().equals(status))) {
				return cancelPagmto(status, ce, paym.getPaymentId());
			}

			//Capturando a Venda Solicitada
			if(StatusPaymentEnum.AUTHORIZED.getId().equals(status)) {
				sale = ce.captureSale(sale.getPayment().getPaymentId());
				status = sale.getStatus() != null ? sale.getStatus() : sale.getPayment().getStatus();
			}
			
			//se o status nao for confirmed_payment, gerar erro
			if(!StatusPaymentEnum.CONFIRMED_PAYMENT.getId().equals(status)) {
				return cancelPagmto(status, ce, paym.getPaymentId());
			}
			
			StatusPaymentEnum stGravar = StatusPaymentEnum.findByCode(status);
			paymentDaoIf.updatePayment(paym.getPaymentId(), stGravar, idUser);
			
		} catch(IOException | CieloRequestException e) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.BAD_GATEWAY.ordinal(), ErrorCodeEnum.ERROR_CONNECT_CIELLO_INIT_PAYMENT, e, e.getMessage())));
			return result;
		}
		
		result.setSuccess(true);
		result.setData(record);
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private RestResultDto cancelPagmto(Integer status, CieloEcommerce ce, String paymentId) {
		
		RestResultDto result = new RestResultDto();
		result.setSuccess(false);
		if(StatusPaymentEnum.DENIED.getId().equals(status)) {
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.CONFLICT.ordinal(), ErrorCodeEnum.ERROR_PAGMTO_DENIED)));
		}
		else if(StatusPaymentEnum.VOIDED.getId().equals(status) || StatusPaymentEnum.ABORTED.getId().equals(status)) {
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.CONFLICT.ordinal(), ErrorCodeEnum.ERROR_PAGMTO_VOIDED)));
		}
		else if(StatusPaymentEnum.REFUNDED.getId().equals(status)) {
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.CONFLICT.ordinal(), ErrorCodeEnum.ERROR_PAGMTO_REFUNDED)));
		}
		else if(StatusPaymentEnum.NOT_FINISHED.getId().equals(status) || StatusPaymentEnum.PENDING.getId().equals(status)) {
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(new AgilizeException(
					HttpStatus.CONFLICT.ordinal(), ErrorCodeEnum.ERROR_PAGMTO_PENDING)));
		}
	
		//ERROR_STATUS_CAPTURE_CIELLO_INVALID
		
		try {
			//a operação tem que ser cancelada
			ce.cancelSale(paymentId);
		}
		catch(CieloRequestException cre) {
			//Abafada
		}
		catch(IOException ioe) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.SERVICE_NOT_PROCESS, ioe);
		}
		return result;
	}
	
	private Payment convertSaleToPayment(Payment record, Sale sale, Integer status, TypeCardEnum typeCard, Long idApplication) {
		
		record.setAuthorizationCode(sale.getPayment().getAuthorizationCode());
		record.setBrand(BrandEnum.findByDescription(record.getSale().getPayment().getCreditCard().getBrand()));
		record.setCardNumber(record.getSale().getPayment().getCreditCard().getCardNumber());
		record.setCustomer(record.getSale().getCustomer().getName());
		//Não Localizado
		record.setEci("");
		//record.setMerchantOrderId(sale.getMerchantOrderId());
		record.setMonthExpirationDateCard(getPeriodExpiration(record.getSale().getPayment().getCreditCard().getExpirationDate(), 0));
		record.setNameAtCard(record.getSale().getPayment().getCreditCard().getHolder());
		record.setPaymentId(sale.getPayment().getPaymentId());
		record.setProofOfSale(sale.getPayment().getProofOfSale());
		record.setSecurityCodeCard(record.getSale().getPayment().getCreditCard().getSecurityCode());
		record.setStatus(StatusPaymentEnum.findByCode(status));
		record.setTid(sale.getPayment().getTid());
		record.setTypeCard(typeCard);
		record.setValue(convertIntegerToBigDecimal(record.getSale().getPayment().getAmount()));
		record.setYearExpirationDateCard(getPeriodExpiration(record.getSale().getPayment().getCreditCard().getExpirationDate(), 1));
		record.setDebitUrlReturn(sale.getPayment().getAuthenticationUrl());
		record.getApplication().setId(idApplication);
		
		return record;
	}
	
	/*private Sale convertPaymentToSale(Payment record) {
		
		Sale sale = new Sale();
		sale.setCustomer(new Customer());
		sale.setPayment(new cieloecommerce.sdk.ecommerce.Payment());
		sale.getPayment().setCreditCard(new CreditCard());
		
		sale.getPayment().setAuthorizationCode(record.getAuthorizationCode());
		sale.getPayment().setPaymentId(record.getPaymentId());
		sale.getPayment().setProofOfSale(record.getProofOfSale());
		sale.getPayment().setTid(record.getTid());
		sale.getPayment().setAmount(convertBigDecimalToInteger(record.getValue()));
		sale.getPayment().getCreditCard().setBrand(record.getBrand().getDescription());
		sale.getPayment().getCreditCard().setCardNumber(record.getCardNumber());
		String month = record.getMonthExpirationDateCard() < 10 ? "0"+record.getMonthExpirationDateCard() : record.getMonthExpirationDateCard().toString();
		sale.getPayment().getCreditCard().setExpirationDate(month+"/"+record.getYearExpirationDateCard());
		sale.getPayment().getCreditCard().setHolder(record.getNameAtCard());
		sale.getPayment().getCreditCard().setSecurityCode(record.getSecurityCodeCard());
		
		sale.getCustomer().setName(record.getCustomer());
		
		sale.setMerchantOrderId(record.getMerchantOrderId());
		
		if(TypeCardEnum.CREDIT.equals(record.getTypeCard())) {
			sale.getPayment().setType(Type.CreditCard);
		}
		else {
			sale.getPayment().setType(Type.DebitCard);
			sale.getPayment().setDebitCard(sale.getPayment().getCreditCard());
			sale.getPayment().setAuthenticationUrl(record.getDebitUrlReturn());
		}
		sale.setStatus(record.getStatus().getId());
		sale.getPayment().setReturnUrl(URL_PAYMENT_RETURN + sale.getPayment().getAmount() + "/" + 
				sale.getMerchantOrderId());
		
		return sale;
	}*/
	
	/**
	 * Validação dos parâmetros de entrada das operações
	 * 1 - Operação com Cartão de Crédito
	 * 2 - Operação com Cartão de Débito
	 * 3 - Operação de Cancelamento
	 * @param record
	 * @param operation
	 * @return
	 */
	private List<AgilizeException> validateInitPayment(Payment record, int operation) {
		
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		if(record == null) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "page.payment.title"));
		}
		else {
			
			/*if(!Util.isNotNull(record.getApplication()) || !Util.isNotNull(record.getApplication().getMerchantId())
					|| !Util.isNotNull(record.getApplication().getMerchantKey())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.application.payment"));
			}*/
			
			/*if(!Util.isNotNull(record.getIdUserCreate())) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.user.create"));
			}*/
			
			if(operation == 3) {
				if(!Util.isNotNull(record.getPaymentId())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.id"));
				}
			}
			
			if(operation == 4) {
				if(!Util.isNotNull(record.getMerchantOrderId())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.merchant.order.id"));
				}
			}

			
			/*if(operation == 2) {
				if(!Util.isNotNull(record.getApplication().getPaymentUrlReturn())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.url.return"));
				}
			}*/
			
			if(operation == 1 || operation == 2) {
				
				if(!Util.isNotNull(record.getMerchantOrderId())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.merchant.order.id"));
				}
				
				if(!Util.isNotNull(record.getSale()) || !Util.isNotNull(record.getSale().getPayment())
						|| !Util.isNotNull(record.getSale().getPayment().getCreditCard()) || !Util.isNotNull(record.getSale().getCustomer())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.sale"));
				}
				else {

					if(!Util.isNotNull(record.getSale().getPayment().getCreditCard().getBrand())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.brand"));
					}
					
					if(!Util.isNotNull(record.getSale().getPayment().getCreditCard().getCardNumber())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.card.number"));
					}
					else if(!RegexCardValidator.isValidCard(record.getSale().getPayment().getCreditCard().getCardNumber(), 
							record.getSale().getPayment().getCreditCard().getBrand())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.payment.card.number"));
					}
					
					if(!Util.isNotNull(record.getSale().getCustomer().getName())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.customer.name"));
					}
					
					if(!Util.isNotNull(record.getSale().getPayment().getCreditCard().getExpirationDate())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.date.expiration"));
					}
					else {
						if(!isValideExpirationPeriod(record.getSale().getPayment().getCreditCard().getExpirationDate())) {
							arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.payment.date.expiration"));
						}
					}
					
					if(!Util.isNotNull(record.getSale().getPayment().getCreditCard().getHolder())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.card.name"));
					}
					if(!Util.isNotNull(record.getSale().getPayment().getCreditCard().getSecurityCode())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.security.code"));
					}
					
					if(!Util.isNotNull(record.getSale().getPayment().getAmount())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.payment.amount"));
					}
					else {
						//TODO
						/*if(record.getSale().getPayment().getAmount() < 500) {
							arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_AMOUNT_SALE));
						}*/
					}
				}
			}
		}
		return arrays;
	}
	
	//@SuppressWarnings({ "rawtypes", "unchecked" })
	private Application findApplicationByName(Payment record) {
		
		Application app = null;
		if(!Util.isNotNull(record.getApplication()) || !Util.isNotNull(record.getApplication().getName())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.application.payment");
		}
		else {
			app = Util.getApplication(record.getApplication().getName(), ServiceAuthenticationFilter.NAME_APP +
					":" + ServiceAuthenticationFilter.KEY);
		}
		return app;
	}
	
	private Integer getPeriodExpiration(String dtExpiration, int period) {
		String[] periods = dtExpiration.split("/");
		return Integer.valueOf(periods[period]);
	}
	
	/**
	 * Validação do campo de expiração do cartão.
	 * Formato: MM/YYYY
	 * @param dtExpiration
	 * @return
	 */
	private boolean isValideExpirationPeriod(String dtExpiration) {
		
		if(dtExpiration.indexOf("/") < 1) {
			return false;
		}
		if(dtExpiration.split("/").length != 2) {
			return false;
		}
		String[] periods = dtExpiration.split("/");
		if(!Util.isNotNull(Util.onlyNumbers(periods[0])) || !Util.isNotNull(Util.onlyNumbers(periods[1]))) {
			return false;
		}
		if(Integer.valueOf(Util.onlyNumbers(periods[0])) > 12) {
			return false;
		}
		if(Integer.valueOf(Util.onlyNumbers(periods[1])) < 1900 || Integer.valueOf(Util.onlyNumbers(periods[1])) > 2100) {
			return false;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(Util.obterDataHoraAtual());
		
		if(c.get(Calendar.YEAR) > Integer.valueOf(Util.onlyNumbers(periods[1]))) {
			return false;
		}
		if(c.get(Calendar.YEAR) == Integer.valueOf(Util.onlyNumbers(periods[1]))) {
			if(c.get(Calendar.MONTH) > Integer.valueOf(Util.onlyNumbers(periods[0])) - 1) {
				return false;
			}
		}
		return true;
	}
	
	private BigDecimal convertIntegerToBigDecimal(Integer amount) {
		String strAmount = amount.toString();
		if(amount < 100) {
			return new BigDecimal("0."+strAmount);
		}
		return new BigDecimal(strAmount.substring(0, strAmount.length()-2)+"."+strAmount.substring(strAmount.length()-2, strAmount.length()));
	}
	
	/*private Integer convertBigDecimalToInteger(BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(100L)).intValue();
	}*/

}
