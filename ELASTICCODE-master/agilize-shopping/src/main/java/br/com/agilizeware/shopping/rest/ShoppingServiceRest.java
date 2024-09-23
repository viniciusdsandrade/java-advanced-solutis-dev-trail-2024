package br.com.agilizeware.shopping.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableFilterParam;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.dto.Pojo;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.dto.SortDTO;
import br.com.agilizeware.enums.ApplicationNamesEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.FilterOperatorEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.File;
import br.com.agilizeware.rest.ServiceRestAb;
import br.com.agilizeware.shopping.ServiceAuthenticationFilter;
import br.com.agilizeware.shopping.model.FeatureFrameEnum;
import br.com.agilizeware.shopping.model.OrderFrameEnum;
import br.com.agilizeware.shopping.model.ProductFrameEnum;
import br.com.agilizeware.shopping.model.TypeProductFrameEnum;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/store")
@Service
public class ShoppingServiceRest extends ServiceRestAb {
	
	@Autowired
	private AppPropertiesService appPropertiesService;
	
	@RequestMapping(value="/filter", method = RequestMethod.GET)
    @ResponseBody
    public RestResultDto<PageableListDTO<Pojo>> findProdcuts(@RequestParam(required = true, name="filter") String filter) {
		
		RestResultDto<PageableListDTO<Pojo>> result = new RestResultDto<PageableListDTO<Pojo>>();
		
		try {
			
			PageableFilterDTO filterProduct = RestResultDto.getMapper().readValue(filter, PageableFilterDTO.class);
			if(!Util.isNotNull(filterProduct)) {
				filterProduct = new PageableFilterDTO();
			}
			
			//Obtendo a aplicação para o Isobrou
			String appTokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			Application appShopping = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, appTokenCaller);
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
					appTokenCaller);
			String urlMongo = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
			
			filterProduct.setSorts(new ArrayList<SortDTO>(1));
			filterProduct.getSorts().add(new SortDTO(ProductFrameEnum.TITLE.getName(), true));
			filterProduct.setParamsFilter(new LinkedHashSet<PageableFilterParam>(1));
			filterProduct.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ENTITY, 
					FilterOperatorEnum.EQ, ProductFrameEnum.COLLECTION.getName()));
			filterProduct.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.APPLICATION.getName(), 
					FilterOperatorEnum.EQ, appShopping.getId()));
			filterProduct.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.AMOUNT.getName(), 
					FilterOperatorEnum.GT, 1));
			
			Map<String, Object> mapProducts = new HashMap<String, Object>(1);
			mapProducts.put("filter", RestResultDto.getMapper().writeValueAsString(filterProduct));
			Object objProducts = Util.accessRestService((urlMongo+"filter/"+ProductFrameEnum.COLLECTION.getName()), 5, null, 
					RestResultDto.class, appElastic.getPassword(), mapProducts);
			PageableListDTO<Pojo> searchProducts = Util.isNotNull(objProducts) ? 
					RestResultDto.getMapper().convertValue(objProducts, new TypeReference<PageableListDTO<Pojo>>() {}) : null;
			result.setSuccess(true);
			result.setData(searchProducts);
		}
		catch(IOException jpe) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, jpe)));
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public RestResultDto<Pojo> saveProdcut(@RequestBody Map<String, Object> mapa) throws JsonProcessingException {
	
		RestResultDto<Pojo> result = new RestResultDto<Pojo>();
		try {
			
			//Validando o Produto a ser salvo
			List<AgilizeException> errors = validateMapOperations(mapa, ProductFrameEnum.values());
			if(Util.isListNotNull(errors)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
				return result;
			}
			//Validando se as chaves das caracteristicas informadas são unicas
			else if(Util.isNotNull(mapa.get(ProductFrameEnum.FEATURES.getName()))) {
				ArrayList lista = RestResultDto.getMapper().convertValue(mapa.get(ProductFrameEnum.FEATURES.getName()), 
						new TypeReference<ArrayList>() {});
				ArrayList<String> keys = new ArrayList<String>(1);
				Map<String, Object> map = null;
				ArrayList<String> keysDuplicated = new ArrayList<String>(1);
				Iterator it = lista.iterator();
				while(it.hasNext()) {
					map = RestResultDto.getMapper().convertValue(it.next(), 
							new TypeReference<HashMap>() {});
					if(keys.contains(map.get(FeatureFrameEnum.KEY.getName()).toString())) {
						keysDuplicated.add(map.get(FeatureFrameEnum.KEY.getName()).toString());
					}
					else {
						keys.add(map.get(FeatureFrameEnum.KEY.getName()).toString());
					}
				}
				
				if(Util.isListNotNull(keysDuplicated)) {
					result.setSuccess(false);
					result.setStrAgilizeExceptionError(RestResultDto.getStrException(
							new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, (String[])keysDuplicated.toArray())));
					return result;
				}
			}
			
			String appTokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), appTokenCaller);
			Application appFileServer = Util.getApplication(ApplicationNamesEnum.FILESERVER.getName(), appTokenCaller);
			Application appShopping = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, appTokenCaller);
			
			if(!Util.isNotNull(appElastic) || !Util.isNotNull(appFileServer) || !Util.isNotNull(appShopping)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.APPLICATION_NOT_FOUND)));
				return result;
			}
			
			//Verificando se não há produto com o mesmo nome para o mesmo estabelecimento
			PageableFilterDTO filter = new PageableFilterDTO();
			filter.setSorts(new ArrayList<SortDTO>(1));
			filter.getSorts().add(new SortDTO("name", true));
			filter.setWithLimitPerPage(false);
			filter.setParamsFilter(new LinkedHashSet<PageableFilterParam>(1));
			filter.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ENTITY, FilterOperatorEnum.EQ, ProductFrameEnum.COLLECTION.getName()));
			filter.getParamsFilter().add(new PageableFilterParam(Pojo.NM_SOUNDEX, 
					FilterOperatorEnum.PHONETIC, Pojo.strSoundex(mapa.get(ProductFrameEnum.TITLE.getName()).toString())));
			filter.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.APPLICATION.getName(), FilterOperatorEnum.EQ, 
					appShopping.getId()));

			if(Util.isNotNull(mapa.get(Pojo.NM_ID))) {
				filter.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ID, FilterOperatorEnum.EQ, mapa.get(Pojo.NM_ID)));
			}
			
			String url = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
			Map map = new HashMap<String, Object>(1);
			map.put("filter", RestResultDto.getMapper().writeValueAsString(filter));
			Object obj = Util.accessRestService((url+"filter/"+ProductFrameEnum.COLLECTION.getName()), 5, null, 
					RestResultDto.class, appElastic.getPassword(), map);
			PageableListDTO<Pojo> search = Util.isNotNull(obj) ? 
					RestResultDto.getMapper().convertValue(obj, new TypeReference<PageableListDTO<Pojo>>() {}) : null;
			
			String strFile = null;
			if(Util.isNotNull(mapa.get(ProductFrameEnum.FILE.getName()))) {
				String urlFile = appFileServer.getHost() + appPropertiesService.getPropertyString("path.file.save.temp.file.service");
				
				List<File> receiveds = RestResultDto.getMapper().convertValue(
						mapa.get(ProductFrameEnum.FILE.getName()), new TypeReference<List<File>>() {});
				obj = Util.accessRestService(urlFile, 2, receiveds, 
						RestResultDto.class, appFileServer.getPassword(), null);
				List<File> files = Util.isNotNull(obj) ? (List<File>)obj : null;
				if(Util.isListNotNull(files)) {
					strFile = RestResultDto.getMapper().writeValueAsString(files);
				}
			}
			if(Util.isNotNull(strFile)) {
				mapa.put(ProductFrameEnum.FILE.getName(), strFile);
			}
			
			mapa.put(ProductFrameEnum.APPLICATION.getName(), appShopping.getId());
			mapa.put(ProductFrameEnum.TYPE.getName(), TypeProductFrameEnum.TYPE_CLOTH);
					
			Pojo pojo = new Pojo();
			pojo.setSoundex(Pojo.strSoundex(mapa.get(ProductFrameEnum.TITLE.getName()).toString()));
			pojo.setNmEntity(ProductFrameEnum.COLLECTION.getName());
			pojo.setCollection(ProductFrameEnum.COLLECTION.getName());
			pojo.setjSon(RestResultDto.getMapper().writeValueAsString(mapa));
		    //Se houver retorno no resultado, alterar o produto. Senão, incluir o produto.
			if(Util.isNotNull(search) && Util.isNotNull(search.getList()) && search.getList().iterator().hasNext()) {
				Pojo pSearch = search.getList().iterator().next();
				//Alterar o produto
				Map<String, Object> update = new HashMap<String, Object>();
				update.put(ProductFrameEnum.AMOUNT.getName(), mapa.get(ProductFrameEnum.AMOUNT.getName()));
				update.put(ProductFrameEnum.DESCRIPTION.getName(), mapa.get(ProductFrameEnum.DESCRIPTION.getName()));
				update.put(ProductFrameEnum.TITLE.getName(), mapa.get(ProductFrameEnum.TITLE.getName()));
				update.put(Pojo.NM_SOUNDEX, pojo.getSoundex());
				update.put(ProductFrameEnum.VALUE.getName(), mapa.get(ProductFrameEnum.VALUE.getName()));
				update.put(ProductFrameEnum.TYPE.getName(), TypeProductFrameEnum.TYPE_CLOTH);
				update.put(ProductFrameEnum.SUBTITLE.getName(), mapa.get(ProductFrameEnum.SUBTITLE.getName()));
				update.put(ProductFrameEnum.FEATURES.getName(), mapa.get(ProductFrameEnum.FEATURES.getName()));
				if(Util.isNotNull(mapa.get(ProductFrameEnum.FILE.getName()))) {
					update.put(ProductFrameEnum.FILE.getName(), mapa.get(ProductFrameEnum.FILE.getName()));
				}
				
				//Realizando a chamada da alteração
				obj = Util.accessRestService((url+pSearch.getId()+"/"+ProductFrameEnum.COLLECTION.getName()), 3, 
						update, RestResultDto.class, appElastic.getPassword(), null);
				pojo.setId(pSearch.getId());
			}
			else {
				//Incluindo o Produto
				obj = Util.accessRestService(url, 2, pojo, RestResultDto.class, appElastic.getPassword(), null);
				pojo = RestResultDto.getMapper().convertValue(obj, new TypeReference<Pojo>() {});
			}
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
	@RequestMapping(value="/order", method = RequestMethod.POST)
    @ResponseBody
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
		String nmProduct = mapProd.get(ProductFrameEnum.TITLE.getName()).toString();
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
			Integer qtdProdOrder = Integer.valueOf(mapProd.get(ProductFrameEnum.AMOUNT_ORDEREDS.getName()).toString());

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

	
}
