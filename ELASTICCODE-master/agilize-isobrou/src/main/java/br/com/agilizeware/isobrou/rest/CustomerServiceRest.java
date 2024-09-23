package br.com.agilizeware.isobrou.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.agilizeware.dto.Pojo;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ApplicationNamesEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.TypeAuthenticationEnum;
import br.com.agilizeware.enums.TypeDeviceEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.isobrou.ServiceAuthenticationFilter;
import br.com.agilizeware.isobrou.model.AddressFrameEnum;
import br.com.agilizeware.isobrou.model.CustomerFrameEnum;
import br.com.agilizeware.isobrou.model.OrderFrameEnum;
import br.com.agilizeware.isobrou.model.ProductFrameEnum;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.File;
import br.com.agilizeware.model.User;
import br.com.agilizeware.rest.ServiceRestAb;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/customer")
@Service
public class CustomerServiceRest extends ServiceRestAb {
	
	@Autowired
	private AppPropertiesService appPropertiesService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	//@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Pojo> saveCustomer(@RequestBody Map<String, Object> customer) { 
	
		RestResultDto<Pojo> result = new RestResultDto<Pojo>();
		Long idUser = null;
		List<File> fileSaved = null;
		Application appFileServer = null;
		String tokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
		String urlSaveUser = appPropertiesService.getPropertyString("url.save.user");
		try {
			
			//Validando o Cliente a ser salvo
			List<AgilizeException> errors = validateMapOperations(customer, CustomerFrameEnum.values());
			if(Util.isListNotNull(errors)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
				return result;
			}
			
			//Salvando a imagem recebida
			String nmFileServer = ApplicationNamesEnum.FILESERVER.getName();
			
			if(Util.isNotNull(customer.get(CustomerFrameEnum.PHOTO.getName()))) {
				appFileServer = Util.getApplication(nmFileServer, tokenCaller);
				String urlFile = appFileServer.getHost() + appPropertiesService.getPropertyString("path.file.save.temp.file.service");
				
				List<File> receiveds = RestResultDto.getMapper().convertValue(
						customer.get(CustomerFrameEnum.PHOTO.getName()), new TypeReference<List<File>>() {});
				Object obj = Util.accessRestService(urlFile, 2, receiveds, 
						RestResultDto.class, appFileServer.getPassword(), null);
				fileSaved = Util.isNotNull(obj) ? (List<File>)obj : null;
			}
			
			Map<String, Object> address = RestResultDto.getMapper().convertValue(
					customer.get(CustomerFrameEnum.ADDRESS.getName()), new TypeReference<HashMap>() {});
			String email = address.get(AddressFrameEnum.EMAIL.getName()).toString();
			//Salvando o Usuário
			User user = new User();
			user.setCpf(customer.get(CustomerFrameEnum.CPF.getName()).toString());
			user.setDevice(TypeDeviceEnum.findByCode(Integer.valueOf(customer.get(CustomerFrameEnum.DEVICE.getName()).toString())));
			user.setDtNascimento(Util.getDateWhitoutHMS(customer.get(CustomerFrameEnum.BIRTHDAY.getName()).toString()));
			user.setEmail(email);
			user.setName(customer.get(CustomerFrameEnum.NAME.getName()).toString());
			user.setPassword(customer.get(CustomerFrameEnum.PASSWORD.getName()).toString());
			user.setTypeAuthentication(TypeAuthenticationEnum.EMAIL);
			user.setUsername(email);
			if(Util.isListNotNull(fileSaved)) {
				user.setFile(fileSaved.get(0));
				customer.put(CustomerFrameEnum.PHOTO.getName(), RestResultDto.getMapper().writeValueAsString(
						fileSaved.get(0)));
			}
			
			User us = null;
		
			Object objUser = Util.accessRestService(urlSaveUser, 2, user, RestResultDto.class, tokenCaller, null);
			if(Util.isNotNull(objUser)) {
				us = RestResultDto.getMapper().convertValue(objUser, new TypeReference<User>() {});
				idUser = us.getId();
				customer.put(CustomerFrameEnum.USER.getName(), idUser);
			}
			
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
					tokenCaller);
			String url = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
			
			Application appIsobrou = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, tokenCaller);
			customer.put(CustomerFrameEnum.APPLICATION.getName(), appIsobrou.getId());
			
			Pojo pojo = new Pojo();
			pojo.setSoundex(Pojo.strSoundex(customer.get(CustomerFrameEnum.NAME.getName()).toString()));  
			pojo.setNmEntity(CustomerFrameEnum.COLLECTION.getName());
			pojo.setCollection(CustomerFrameEnum.COLLECTION.getName());
			pojo.setjSon(RestResultDto.getMapper().writeValueAsString(customer));
			//Incluindo o Cliente
			Object objCustomer = Util.accessRestService(url, 2, pojo, RestResultDto.class, appElastic.getPassword(), null);
			pojo = Util.isNotNull(objCustomer) ? RestResultDto.getMapper().convertValue(objCustomer, new TypeReference<Pojo>() {}) : null;
			
			result.setSuccess(true);
			result.setData(pojo);
			
		} catch(IOException | AgilizeException | ResourceAccessException e) {
			
			log.error("Erro durante o cadastro do usuário = "+e.getMessage(), e);
			
			//Deletando o arquivo de imagem que foi salvo
			if(Util.isListNotNull(fileSaved)) {
				String urlDeleteFile = appFileServer.getHost() + appPropertiesService.getPropertyString("path.file.delete.service") +
						fileSaved.get(0).getId();
				try {
					Util.accessRestService(urlDeleteFile, 4, null, RestResultDto.class, appFileServer.getPassword(), null);
				}
				catch(Throwable th) {
					//Abafada
					log.error("Erro ao deletar Arquivo = "+th.getMessage(), th);
				}
			}
			
			//Deletando o usuário que foi salvo
			if(Util.isNotNull(idUser)) {
				String urlDeleteUser = urlSaveUser + "/" + idUser;
				try {
					Map<String, String> headers = new HashMap<String, String>(2);
					headers.put(Util.HEADER_PATH_KEY, urlDeleteUser); 
					headers.put(Util.HEADER_APPLICATION_KEY, ServiceAuthenticationFilter.NAME_APP); 
					Util.accessRestHeaderService(urlDeleteUser, 4, null, RestResultDto.class, tokenCaller, null, headers);
				}
				catch(Throwable th) {
					//Abafada
					log.error("Erro ao deletar Usuário = "+th.getMessage(), th);
				}
			}
			
			result.setSuccess(false);
			if(e instanceof AgilizeException) {
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(e));
			}
			else if (e instanceof ResourceAccessException) {
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.SERVICE_NOT_PROCESS, e)));
			}
			else {
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.ERROR_JSON, e)));
			}
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="/order", method = RequestMethod.POST)
    @ResponseBody
	//@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Pojo> saveOrder(@RequestBody Map<String, Object> order) { 
	
		RestResultDto<Pojo> result = new RestResultDto<Pojo>();
		try {
			
			//Validando o Cliente a ser salvo
			List<AgilizeException> errors = validateMapOperations(order, OrderFrameEnum.values());
			if(Util.isListNotNull(errors)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
				return result;
			}
			
			String tokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
					tokenCaller);
			String url = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
			
			Application appIsobrou = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, tokenCaller);
			order.put(OrderFrameEnum.APPLICATION.getName(), appIsobrou.getId());
			
			order.put(OrderFrameEnum.DT_ORDER.getName(), Util.getStringDateWithHour(Util.obterDataHoraAtual()));
			
			//Sumarizar todos os produtos
			ArrayList lista = RestResultDto.getMapper().convertValue(order.get(OrderFrameEnum.ORDERS.getName()), 
					new TypeReference<ArrayList>() {});
			Iterator it = lista.iterator();
			BigDecimal totalValue = new BigDecimal("0");
			while(it.hasNext()) {
				Map<String, Object> map = RestResultDto.getMapper().convertValue(it.next(), 
						new TypeReference<HashMap>() {});
				BigDecimal value = new BigDecimal(map.get(ProductFrameEnum.VALUE.getName()).toString());
				
				//Verificando se tem produto no estoque
				try {
					validateQtdEstoq(url, map, appElastic, value);
					totalValue = totalValue.add(value);
				}
				catch(AgilizeException age) {
					result.setSuccess(false);
					result.setStrAgilizeExceptionError(RestResultDto.getStrException(age));
					return result;
				}
			}
			
			order.put(OrderFrameEnum.TOTAL_VALUE.getName(), totalValue);
			
			//Salvando o Pedido
			Pojo pojo = new Pojo();
			pojo.setSoundex(null);  
			pojo.setNmEntity(OrderFrameEnum.COLLECTION.getName());
			pojo.setCollection(OrderFrameEnum.COLLECTION.getName());
			pojo.setjSon(RestResultDto.getMapper().writeValueAsString(order));
			//Incluindo o Pedido
			Object objOrder = Util.accessRestService(url, 2, pojo, RestResultDto.class, appElastic.getPassword(), null);
			pojo = Util.isNotNull(objOrder) ? RestResultDto.getMapper().convertValue(objOrder, new TypeReference<Pojo>() {}) : null;
			
			result.setSuccess(true);
			result.setData(pojo);
			
		} catch(IOException e) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, e)));
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="/validate/preOrder/{id}", method = RequestMethod.GET)
    @ResponseBody
	//@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Boolean> validatePreOrder(@PathVariable(value="id", required=true) String id) {
		
		RestResultDto<Boolean> result = new RestResultDto<Boolean>();
		try {
			String tokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
					tokenCaller);
			String urlId = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo") +
					id + "/" + OrderFrameEnum.COLLECTION.getName();
			
			Object objOrder = Util.accessRestService(urlId, 5, null, RestResultDto.class, appElastic.getPassword(), null);
			if(Util.isNotNull(objOrder)) {
				Pojo order = RestResultDto.getMapper().convertValue(objOrder, new TypeReference<Pojo>() {});
				Map<String, Object> mapOrder = RestResultDto.getMapper().readValue(order.getjSon(), 
						new TypeReference<HashMap>() {});
				//Obtendo a lista de produtos do pedido
				ArrayList lista = RestResultDto.getMapper().convertValue(mapOrder.get(OrderFrameEnum.ORDERS.getName()), 
						new TypeReference<ArrayList>() {});
				Iterator it = lista.iterator();
				while(it.hasNext()) {
					Map<String, Object> map = RestResultDto.getMapper().convertValue(it.next(), 
							new TypeReference<HashMap>() {});
					
					//Verificando se tem produto no estoque
					try {
						validateQtdEstoq(appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo"), map, appElastic, null);
					}
					catch(AgilizeException age) {
						result.setSuccess(false);
						result.setData(false);
						result.setStrAgilizeExceptionError(RestResultDto.getStrException(age));
						return result;
					}
				}
			}
			else {
				//Exceção de Pedido não localidado
				result.setSuccess(false);
				result.setData(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ID_NOT_FOUND, 
								id, OrderFrameEnum.COLLECTION.getName())));
				return result;
			}
		
		}
		catch(IOException ioe) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe)));
			result.setData(false);
			return result;
		}
		
		result.setSuccess(true);
		result.setData(true);
		return result;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="/process/posPayment/{id}", method = RequestMethod.GET)
    @ResponseBody
	//@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Boolean> processPosPayment(@PathVariable(value="id", required=true) String id) {
		
		RestResultDto<Boolean> result = new RestResultDto<Boolean>();
		try {
			String tokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
					tokenCaller);
			String urlOrder = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo") +
					id + "/" + OrderFrameEnum.COLLECTION.getName();
			
			Object objOrder = Util.accessRestService(urlOrder, 5, null, RestResultDto.class, appElastic.getPassword(), null);
			if(Util.isNotNull(objOrder)) {
				Pojo order = RestResultDto.getMapper().convertValue(objOrder, new TypeReference<Pojo>() {});
				Map<String, Object> mapOrder = RestResultDto.getMapper().readValue(order.getjSon(), 
						new TypeReference<HashMap>() {});
				//Obtendo a lista de produtos do pedido
				ArrayList lista = RestResultDto.getMapper().convertValue(mapOrder.get(OrderFrameEnum.ORDERS.getName()), 
						new TypeReference<ArrayList>() {});
				Iterator it = lista.iterator();
				while(it.hasNext()) {
					Map<String, Object> map = RestResultDto.getMapper().convertValue(it.next(), 
							new TypeReference<HashMap>() {});
					
					//Atualizando a quantidade de produtos no estoque
					String urlIdProd = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo") + 
							map.get(Pojo.NM_ID) + "/" + ProductFrameEnum.COLLECTION.getName();
					Object objProduct = Util.accessRestService(urlIdProd, 5, null, RestResultDto.class, appElastic.getPassword(), null);
					if(Util.isNotNull(objProduct)) {
						Pojo product = RestResultDto.getMapper().convertValue(objProduct, new TypeReference<Pojo>() {});
						Map<String, Object> mapProduct = RestResultDto.getMapper().readValue(product.getjSon(), 
								new TypeReference<HashMap>() {});
						Integer qtdProdEstoq = Integer.valueOf(mapProduct.get(ProductFrameEnum.AMOUNT.getName()).toString());
						Integer qtdProdOrder = Integer.valueOf(map.get(ProductFrameEnum.AMOUNT.getName()).toString());
						
						Map<String, Object> mpUpdt = new HashMap<String, Object>(1);
						mpUpdt.put(ProductFrameEnum.AMOUNT.getName(), qtdProdEstoq - qtdProdOrder);
						
						Util.accessRestService(urlIdProd, 3, mpUpdt, RestResultDto.class, appElastic.getPassword(), null);
					}
					else {
						//Exceção de produto não localidado
						result.setSuccess(false);
						result.setData(false);
						result.setStrAgilizeExceptionError(RestResultDto.getStrException(
								new AgilizeException(HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ID_NOT_FOUND, 
								map.get(Pojo.NM_ID).toString(), ProductFrameEnum.COLLECTION.getName())));
						return result;
					}
				}
			}
			else {
				//Exceção de Pedido não localidado
				result.setSuccess(false);
				result.setData(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ID_NOT_FOUND, 
								id, OrderFrameEnum.COLLECTION.getName())));
				return result;
			}
		}
		catch(IOException ioe) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe)));
			result.setData(false);
			return result;
		}	
		
		result.setSuccess(true);
		result.setData(true);
		return result;
	}

	
	
	/**
	 * Validando se há produtos suficientes no Estoque
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Boolean validateQtdEstoq(String baseUrl, Map<String, Object> mapProd, Application appElastic, BigDecimal value) {
		//Verificando se tem produto no estoque
		String urlId = baseUrl + mapProd.get(Pojo.NM_ID) + "/" + ProductFrameEnum.COLLECTION.getName();
		String nmProduct = mapProd.get(ProductFrameEnum.NAME.getName()).toString();
		Object objProduct = Util.accessRestService(urlId, 5, null, RestResultDto.class, appElastic.getPassword(), null);
		if(Util.isNotNull(objProduct)) {
			Pojo product = RestResultDto.getMapper().convertValue(objProduct, new TypeReference<Pojo>() {});
			Map<String, Object> mapProduct = null;
			try {
				mapProduct = RestResultDto.getMapper().readValue(product.getjSon(),
						new TypeReference<HashMap>() {});
			}
			catch(IOException ioe) {
				throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe);
			
			}
			Integer qtdProdEstoq = Integer.valueOf(mapProduct.get(ProductFrameEnum.AMOUNT.getName()).toString());
			Integer qtdProdOrder = Integer.valueOf(mapProd.get(ProductFrameEnum.AMOUNT.getName()).toString());

			if(qtdProdOrder > qtdProdEstoq) {
				//Exceção de estoque nao disponivel
				throw new AgilizeException(HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ERROR_PRODUCT_NOT_SUFFICIENT, 
								qtdProdEstoq.toString(), nmProduct);
			}
			
			if(Util.isNotNull(value)) {
				BigDecimal valueBD = new BigDecimal(mapProduct.get(ProductFrameEnum.VALUE.getName()).toString());
				if(valueBD.compareTo(value) > 0) {
					//Exceção de valor do produto informado menor que o gravado
					throw new AgilizeException(HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ERROR_PRODUCT_NOT_SUFFICIENT, 
									qtdProdEstoq.toString(), nmProduct);
				}
			}
			
		}
		else {
			//Exceção de produto não localidado
			throw new AgilizeException(HttpStatus.NOT_ACCEPTABLE.ordinal(), ErrorCodeEnum.ID_NOT_FOUND, 
					mapProd.get(Pojo.NM_ID).toString(), ProductFrameEnum.COLLECTION.getName());
		}
		return true;
	}
	
	/*@SuppressWarnings("unchecked")
	private List<AgilizeException> validateCustomer(Map<String, Object> record) throws IOException {
		
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		if(!Util.isMapNotNull(record)) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "page.customer.save.title"));
		}
		else {
			if(!Util.isNotNull(record.get(CustomerFrameEnum.NAME.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.name"));
			}
			else if (record.get(CustomerFrameEnum.NAME.getName()).toString().length() > CustomerFrameEnum.NAME.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.name", 
						CustomerFrameEnum.NAME.getLength().toString()));
			}
			
			if(!Util.isNotNull(record.get(CustomerFrameEnum.RG.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.rg"));
			}
			else if (record.get(CustomerFrameEnum.RG.getName()).toString().length() > CustomerFrameEnum.RG.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.rg", 
						CustomerFrameEnum.RG.getLength().toString()));
			}
			
			if(!Util.isNotNull(record.get(CustomerFrameEnum.CPF.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.cpf"));
			}
			else if (record.get(CustomerFrameEnum.CPF.getName()).toString().length() > CustomerFrameEnum.CPF.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.cpf", 
						CustomerFrameEnum.CPF.getLength().toString()));
			}
			
			if(!Util.isNotNull(record.get(CustomerFrameEnum.PASSWORD.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.password"));
			}
			else if (record.get(CustomerFrameEnum.PASSWORD.getName()).toString().length() != CustomerFrameEnum.PASSWORD.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.password", 
						CustomerFrameEnum.PASSWORD.getLength().toString()));
			}
			
			if(!Util.isNotNull(Util.onlyNumbers(record.get(CustomerFrameEnum.DEVICE.getName()).toString()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.device"));
			}
			else {
				try {
					TypeDeviceEnum.findByCode(Integer.valueOf(record.get(CustomerFrameEnum.DEVICE.getName()).toString()));
				}
				catch(ArrayIndexOutOfBoundsException arr) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, arr, "lbl.device"));
				}
			}
			
			if (Util.isNotNull(record.get(CustomerFrameEnum.BIRTHDAY.getName()))) {
				if(record.get(CustomerFrameEnum.BIRTHDAY.getName()).toString().length() != CustomerFrameEnum.BIRTHDAY.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.birthday"));
				}
				try {
					Util.getDateWhitoutHMS(record.get(CustomerFrameEnum.BIRTHDAY.getName()).toString());
				}
				catch(RuntimeException re) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, re, "lbl.birthday"));
				}
			}
			
			if(!Util.isNotNull(record.get(CustomerFrameEnum.ADDRESS.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.address"));
			}
			else {
				
				Map<String, Object> address = RestResultDto.getMapper().readValue(
						record.get(CustomerFrameEnum.ADDRESS.getName()).toString(), HashMap.class);
				
				if (Util.isNotNull(address.get(AddressFrameEnum.RESIDENCIAL_PHONE.getName())) &&
						(!Util.isNotNull(Util.onlyNumbers(address.get(AddressFrameEnum.RESIDENCIAL_PHONE.getName()).toString())) ||
						  Util.onlyNumbers(address.get(AddressFrameEnum.RESIDENCIAL_PHONE.getName()).toString()).length() > AddressFrameEnum.RESIDENCIAL_PHONE.getLength())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.residencial.phone"));
				}
				
				if (Util.isNotNull(address.get(AddressFrameEnum.CELL_PHONE.getName())) &&
						(!Util.isNotNull(Util.onlyNumbers(address.get(AddressFrameEnum.CELL_PHONE.getName()).toString())) ||
						  Util.onlyNumbers(address.get(AddressFrameEnum.CELL_PHONE.getName()).toString()).length() > AddressFrameEnum.CELL_PHONE.getLength())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.cell.phone"));
				}
				
				if(!Util.isNotNull(address.get(AddressFrameEnum.EMAIL.getName()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.email"));
				}
				else if (address.get(AddressFrameEnum.EMAIL.getName()).toString().length() > AddressFrameEnum.EMAIL.getLength() ||
					  !Util.isValidEmail(address.get(AddressFrameEnum.EMAIL.getName()).toString())) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, "lbl.email"));
				}
				
				if(!Util.isNotNull(Util.onlyNumbers(address.get(AddressFrameEnum.ZIP_CODE.getName()).toString()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.cep"));
				}
				else if (Util.onlyNumbers(address.get(AddressFrameEnum.ZIP_CODE.getName()).toString()).length() != AddressFrameEnum.ZIP_CODE.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.cep", 
							AddressFrameEnum.ZIP_CODE.getLength().toString()));
				}
				
				if(!Util.isNotNull(address.get(AddressFrameEnum.STREET.getName()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.street"));
				}
				else if (address.get(AddressFrameEnum.STREET.getName()).toString().length() > AddressFrameEnum.STREET.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.street", 
							AddressFrameEnum.STREET.getLength().toString()));
				}
				
				if(!Util.isNotNull(Util.onlyNumbers(address.get(AddressFrameEnum.NUMBER.getName()).toString()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.number"));
				}
				else if (Util.onlyNumbers(address.get(AddressFrameEnum.NUMBER.getName()).toString()).length() > AddressFrameEnum.NUMBER.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.number", 
							AddressFrameEnum.NUMBER.getLength().toString()));
				}
				
				if (Util.isNotNull(address.get(AddressFrameEnum.COMPLEMENT.getName())) &&
						address.get(AddressFrameEnum.COMPLEMENT.getName()).toString().length() > AddressFrameEnum.COMPLEMENT.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.complement", 
							AddressFrameEnum.COMPLEMENT.getLength().toString()));
				}
				
				if (Util.isNotNull(address.get(AddressFrameEnum.REFERENCE_POINT.getName())) &&
						address.get(AddressFrameEnum.REFERENCE_POINT.getName()).toString().length() > AddressFrameEnum.REFERENCE_POINT.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.reference.point", 
							AddressFrameEnum.REFERENCE_POINT.getLength().toString()));
				}
				
				if(!Util.isNotNull(address.get(AddressFrameEnum.NEIGHBORHOOD.getName()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.neighborhood"));
				}
				else if (address.get(AddressFrameEnum.NEIGHBORHOOD.getName()).toString().length() > AddressFrameEnum.NEIGHBORHOOD.getLength()) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.neighborhood", 
							AddressFrameEnum.NEIGHBORHOOD.getLength().toString()));
				}
				
				if(!Util.isNotNull(address.get(AddressFrameEnum.CITY.getName()))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.county"));
				}
			}
		}
		return arrays;
	}*/
	
}
