package br.com.agilizeware.payment.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.enums.StatusPaymentEnum;
import br.com.agilizeware.model.HistoricPayment;
import br.com.agilizeware.model.Payment;


@Component("paymentDaoIf")
public class PaymentDaoImpl extends DaoAB<Payment, Long> {
	
	@Autowired
	private HistoricPaymentDaoRepository historicRepository;
	
	@Autowired
	public PaymentDaoImpl(PaymentDaoRepository repository) {
		super(Payment.class, repository);
    }
	
	public Payment updatePayment(String paymentId, StatusPaymentEnum stEnum, Long idUser) {
		
		Payment pay = ((PaymentDaoRepository)getRepositorio()).findByPaymentId(paymentId);
		
		HistoricPayment hist = new HistoricPayment();
		hist.setApplication(pay.getApplication());
		hist.setAuthorizationCode(pay.getAuthorizationCode());
		hist.setBrand(pay.getBrand());
		hist.setCardNumber(pay.getCardNumber());
		hist.setCustomer(pay.getCustomer());
		hist.setDtCreate(pay.getDtCreate());
		hist.setEci(pay.getEci());
		hist.setIdUserCreate(pay.getIdUserCreate());
		hist.setMonthExpirationDateCard(pay.getMonthExpirationDateCard());
		hist.setNameAtCard(pay.getNameAtCard());
		hist.setPayment(pay);
		hist.setPaymentId(pay.getPaymentId());
		hist.setProofOfSale(pay.getProofOfSale());
		hist.setSecurityCodeCard(pay.getSecurityCodeCard());
		hist.setStatus(pay.getStatus());
		hist.setTid(pay.getTid());
		hist.setTypeCard(pay.getTypeCard());
		hist.setValue(pay.getValue());
		hist.setYearExpirationDateCard(pay.getYearExpirationDateCard());
		hist.setMerchantOrderId(pay.getMerchantOrderId());
		hist.setDebitUrlReturn(pay.getDebitUrlReturn());
		
		historicRepository.save(hist);
		
		pay.setStatus(stEnum);
		pay.setIdUserCreate(idUser);
		
		super.save(pay);
		
		return pay;
	}
	
	public Payment findByMerchantOrderId(String merchantOrderId) {
		return ((PaymentDaoRepository)super.getRepositorio()).findByMerchantOrderId(merchantOrderId);
	}

}
