package br.com.agilizeware.isobrou.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

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
import br.com.agilizeware.isobrou.ServiceAuthenticationFilter;
import br.com.agilizeware.isobrou.model.AddressFilterFrameEnum;
import br.com.agilizeware.isobrou.model.AddressFrameEnum;
import br.com.agilizeware.isobrou.model.CustomerFrameEnum;
import br.com.agilizeware.isobrou.model.LocationFrameEnum;
import br.com.agilizeware.isobrou.model.ProductFilterFrameEnum;
import br.com.agilizeware.isobrou.model.ProductFrameEnum;
import br.com.agilizeware.isobrou.model.StoreFrameEnum;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.File;
import br.com.agilizeware.rest.ServiceRestAb;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/product")
@Service
public class ProductServiceRest extends ServiceRestAb {
	
	@Autowired
	private AppPropertiesService appPropertiesService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	//@org.springframework.transaction.annotation.Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Pojo> saveProdcut(@RequestBody Map<String, Object> mapa) throws JsonProcessingException {
	
		RestResultDto<Pojo> result = new RestResultDto<Pojo>();
		try {
			
			//Convertendo o Json recebido em Map //new ObjectMapper()
			//Map<String, Object> mapa = RestResultDto.getMapper().readValue(record, HashMap.class);
			//Validando o Produto a ser salvo
			List<AgilizeException> errors = validateMapOperations(mapa, ProductFrameEnum.values());
			if(Util.isListNotNull(errors)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
				return result;
			}
			
			String appTokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			String nmEntidade = ApplicationNamesEnum.ELASTICCODE.getName();
			String nmFileServer = ApplicationNamesEnum.FILESERVER.getName();
			
			Application app = Util.getApplication(nmEntidade, appTokenCaller);
			Application appFileServer = Util.getApplication(nmFileServer, appTokenCaller);
			Application appIsobrou = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, appTokenCaller);

			if(!Util.isNotNull(app) || !Util.isNotNull(appFileServer) || !Util.isNotNull(appIsobrou)) {
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
			filter.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.STORE.getName(), FilterOperatorEnum.EQ, 
					mapa.get(ProductFrameEnum.STORE.getName())));
			filter.getParamsFilter().add(new PageableFilterParam(Pojo.NM_SOUNDEX, 
					FilterOperatorEnum.PHONETIC, Pojo.strSoundex(mapa.get(ProductFrameEnum.NAME.getName()).toString())));
			filter.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.APPLICATION.getName(), FilterOperatorEnum.EQ, 
					appIsobrou.getId()));

			if(Util.isNotNull(mapa.get(Pojo.NM_ID))) {
				filter.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ID, FilterOperatorEnum.EQ, mapa.get(Pojo.NM_ID)));
			}
			
			
			String url = app.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
			Map map = new HashMap<String, Object>(1);
			map.put("filter", RestResultDto.getMapper().writeValueAsString(filter));
			Object obj = Util.accessRestService((url+"filter/"+ProductFrameEnum.COLLECTION.getName()), 5, null, 
					RestResultDto.class, app.getPassword(), map);
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
			
			mapa.put(ProductFrameEnum.APPLICATION.getName(), appIsobrou.getId());
					
			Pojo pojo = new Pojo();
			pojo.setSoundex(Pojo.strSoundex(mapa.get(ProductFrameEnum.NAME.getName()).toString()));
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
				update.put(ProductFrameEnum.DT_EXPIRATION.getName(), mapa.get(ProductFrameEnum.DT_EXPIRATION.getName()));
				update.put(ProductFrameEnum.NAME.getName(), mapa.get(ProductFrameEnum.NAME.getName()));
				update.put(Pojo.NM_SOUNDEX, pojo.getSoundex());
				update.put(ProductFrameEnum.VALUE.getName(), mapa.get(ProductFrameEnum.VALUE.getName()));
				if(Util.isNotNull(mapa.get(ProductFrameEnum.FILE.getName()))) {
					update.put(ProductFrameEnum.FILE.getName(), mapa.get(ProductFrameEnum.FILE.getName()));
				}

				
				//Realizando a chamada da alteração
				obj = Util.accessRestService((url+pSearch.getId()+"/"+ProductFrameEnum.COLLECTION.getName()), 3, 
						update, RestResultDto.class, app.getPassword(), null);
				pojo.setId(pSearch.getId());
			}
			else {
				//Incluindo o Produto
				obj = Util.accessRestService(url, 2, pojo, RestResultDto.class, app.getPassword(), null);
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/filter", method = RequestMethod.GET)
    @ResponseBody
	//@org.springframework.transaction.annotation.Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<PageableListDTO<Pojo>> findProdcuts(@RequestParam(required = true, name="parameters") String parameters) {
		
		RestResultDto<PageableListDTO<Pojo>> result = new RestResultDto<PageableListDTO<Pojo>>();
		
		try {
			Map<String, Object> filter = null;
			
			try {
				filter = RestResultDto.getMapper().readValue(parameters, HashMap.class);
			}
			catch(JsonMappingException jme) {
				filter = RestResultDto.getMapper().convertValue(parameters, new TypeReference<HashMap>() {});
			}

			//Validando o Filtro a ser utilizado na Pesquisa
			List<AgilizeException> errors = validateMapOperations(filter, ProductFilterFrameEnum.values());
			if(Util.isListNotNull(errors)) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getStrException(
						new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.ERROR_FIELDS, errors)));
				return result;
			}
			
			//Obtendo a aplicação para o Isobrou
			String appTokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
			Application appIsobrou = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, appTokenCaller);
			Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
					appTokenCaller);
			String urlMongo = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
			
			Map<String, Object> mapAddress = null;
			Map<String, Object> mapLocation = null;
			Object objStores = null;
			
			if(Util.isNotNull(filter.get(ProductFilterFrameEnum.ADDRESS.getName()))) {
				mapAddress = RestResultDto.getMapper().readValue(
						filter.get(ProductFilterFrameEnum.ADDRESS.getName()).toString(), HashMap.class);
				if(Util.isNotNull(mapAddress.get(AddressFilterFrameEnum.LOCATION.getName()))) {
					mapLocation = RestResultDto.getMapper().readValue(
							filter.get(AddressFilterFrameEnum.LOCATION.getName()).toString(), HashMap.class);
				}
			}
			
			PageableFilterDTO filterStore = new PageableFilterDTO();
			filterStore.setSorts(new ArrayList<SortDTO>(1));
			filterStore.getSorts().add(new SortDTO(StoreFrameEnum.COMPANY_NAME.getName(), true));
			filterStore.setWithLimitPerPage(false);
			filterStore.setParamsFilter(new LinkedHashSet<PageableFilterParam>(1));
			filterStore.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ENTITY, 
					FilterOperatorEnum.EQ, StoreFrameEnum.COLLECTION.getName()));
			filterStore.getParamsFilter().add(new PageableFilterParam(StoreFrameEnum.APPLICATION.getName(), 
					FilterOperatorEnum.EQ, appIsobrou.getId()));
			
			if(!((Boolean)filter.get(ProductFilterFrameEnum.NO_ADDRESS.getName()))) {
				boolean findOut = false;
				if(Util.isMapNotNull(mapLocation)) {
					filterStore.getParamsFilter().add(new PageableFilterParam(
						StoreFrameEnum.ADDRESS.getName()+"."+AddressFrameEnum.LOCATION.getName()+"."+LocationFrameEnum.LATITUDE.getName(), 
						FilterOperatorEnum.EQ, mapLocation.get(LocationFrameEnum.LATITUDE.getName())));
					filterStore.getParamsFilter().add(new PageableFilterParam(
							StoreFrameEnum.ADDRESS.getName()+"."+AddressFrameEnum.LOCATION.getName()+"."+LocationFrameEnum.LONGITUDE.getName(), 
							FilterOperatorEnum.EQ, mapLocation.get(LocationFrameEnum.LONGITUDE.getName())));
					findOut = true;
				}
				else {
					
					PageableFilterDTO filterCustomer = new PageableFilterDTO();
					filterCustomer.setWithLimitPerPage(false);
					filterCustomer.setParamsFilter(new LinkedHashSet<PageableFilterParam>(1));
					filterCustomer.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ENTITY, 
							FilterOperatorEnum.EQ, CustomerFrameEnum.COLLECTION.getName()));
					filterCustomer.getParamsFilter().add(new PageableFilterParam(CustomerFrameEnum.APPLICATION.getName(), 
							FilterOperatorEnum.EQ, appIsobrou.getId()));
					filterCustomer.getParamsFilter().add(new PageableFilterParam(CustomerFrameEnum.USER.getName(), 
							FilterOperatorEnum.EQ, filter.get(ProductFilterFrameEnum.USER.getName())));
					
					Map<String, Object> map = new HashMap<String, Object>(1);
					map.put("filter", RestResultDto.getMapper().writeValueAsString(filterCustomer));
					Object obj = Util.accessRestService((urlMongo+"filter/"+CustomerFrameEnum.COLLECTION.getName()), 5, null, 
							RestResultDto.class, appElastic.getPassword(), map);
					if(Util.isNotNull(obj)) {
						PageableListDTO<Pojo> searchUser = 
								RestResultDto.getMapper().convertValue(obj, new TypeReference<PageableListDTO<Pojo>>() {});
						if(searchUser.getList() != null && searchUser.getList().iterator().hasNext()) {
							Pojo pojoCostumer = searchUser.getList().iterator().next();
							Map<String, Object> mapCustomer = RestResultDto.getMapper().readValue(
									pojoCostumer.getjSon(), HashMap.class);
							Map<String, Object> mapAddCustomer = RestResultDto.getMapper().readValue(
									mapCustomer.get(CustomerFrameEnum.ADDRESS.getName()).toString(), HashMap.class);
							if(Util.isNotNull(mapAddCustomer.get(AddressFrameEnum.LOCATION.getName()))) {
								Map<String, Object> mapAddLocationCustomer = RestResultDto.getMapper().readValue(
										mapAddCustomer.get(AddressFrameEnum.LOCATION.getName()).toString(), HashMap.class);
								filterStore.getParamsFilter().add(new PageableFilterParam(
										StoreFrameEnum.ADDRESS.getName()+"."+AddressFrameEnum.LOCATION.getName()+"."+LocationFrameEnum.LATITUDE.getName(), 
										FilterOperatorEnum.EQ, mapAddLocationCustomer.get(LocationFrameEnum.LATITUDE.getName())));
								filterStore.getParamsFilter().add(new PageableFilterParam(
										StoreFrameEnum.ADDRESS.getName()+"."+AddressFrameEnum.LOCATION.getName()+"."+LocationFrameEnum.LONGITUDE.getName(), 
										FilterOperatorEnum.EQ, mapAddLocationCustomer.get(LocationFrameEnum.LONGITUDE.getName())));
								findOut = true;
							}
							
						}
					}
				}
				if(!findOut) {
					result.setSuccess(false);
					result.setStrAgilizeExceptionError(RestResultDto.getStrException(
							new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.ERROR_LOCATION_SEARCH_NOT_FOUND)));
					return result;
				}
			
				Map<String, Object> mapFilterStore = new HashMap<String, Object>(1);
				mapFilterStore.put("filter", RestResultDto.getMapper().writeValueAsString(filterStore));
				objStores = Util.accessRestService((urlMongo+"filter/"+StoreFrameEnum.COLLECTION.getName()), 5, null, 
						RestResultDto.class, appElastic.getPassword(), mapFilterStore);
			}
			
			PageableFilterDTO filterProduct = new PageableFilterDTO();
			filterProduct.setSorts(new ArrayList<SortDTO>(1));
			filterProduct.getSorts().add(new SortDTO(ProductFrameEnum.NAME.getName(), true));
			//filterProduct.setWithLimitPerPage(false);
			filterProduct.setParamsFilter(new LinkedHashSet<PageableFilterParam>(1));
			filterProduct.getParamsFilter().add(new PageableFilterParam(Pojo.NM_ENTITY, 
					FilterOperatorEnum.EQ, ProductFrameEnum.COLLECTION.getName()));
			filterProduct.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.APPLICATION.getName(), 
					FilterOperatorEnum.EQ, appIsobrou.getId()));
			
			if(Util.isNotNull(filter.get(ProductFilterFrameEnum.NAME.getName()))) {
				filterProduct.getParamsFilter().add(new PageableFilterParam(Pojo.NM_SOUNDEX, 
						FilterOperatorEnum.PHONETIC, Pojo.strSoundex(filter.get(ProductFilterFrameEnum.NAME.getName()).toString())));
			}
			if(Util.isNotNull(filter.get(ProductFilterFrameEnum.TYPE.getName()))) {
				filterProduct.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.TYPE.getName(), 
						FilterOperatorEnum.EQ, filter.get(ProductFilterFrameEnum.TYPE.getName())));
			}
			
			if(Util.isNotNull(objStores)) {
				StringBuffer inList = new StringBuffer();
				PageableListDTO<Pojo> searchStore = 
						RestResultDto.getMapper().convertValue(objStores, new TypeReference<PageableListDTO<Pojo>>() {});
				if(searchStore.getList() != null && searchStore.getList().iterator().hasNext()) {
					while(searchStore.getList().iterator().hasNext()) {
						Pojo pStore = searchStore.getList().iterator().next();
						inList.append(pStore.getjSon());
						if(searchStore.getList().iterator().hasNext()) {
							inList.append(", ");
						}
					}
				}
				if(Util.isNotNull(inList.toString())) {
					filterProduct.getParamsFilter().add(new PageableFilterParam(ProductFrameEnum.STORE.getName(), 
							FilterOperatorEnum.IN, inList.toString()));
				}
			}
			
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
	
	
	
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<AgilizeException> validateOperations(Map<String, Object> record, int operation) throws IOException {
		
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		if(!Util.isMapNotNull(record)) {
			arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "page.product.save.title"));
		}
		else {
			if(!Util.isNotNull(record.get(ProductFrameEnum.NAME.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.name"));
			}
			else if (record.get(ProductFrameEnum.NAME.getName()).toString().length() > ProductFrameEnum.NAME.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.name", 
						ProductFrameEnum.NAME.getLength().toString()));
			}
			
			if(!Util.isNotNull(record.get(ProductFrameEnum.DESCRIPTION.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.description"));
			}
			else if (record.get(ProductFrameEnum.DESCRIPTION.getName()).toString().length() > ProductFrameEnum.DESCRIPTION.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.description", 
						ProductFrameEnum.DESCRIPTION.getLength().toString()));
			}
			
			if(!Util.isNotNull(record.get(ProductFrameEnum.TYPE.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.product.type"));
			}
			else { 
				Map<String, Object> mapa = (Collections.synchronizedMap((LinkedHashMap)record.get(ProductFrameEnum.TYPE.getName())));
				if(!Util.isNotNull(mapa.get(Pojo.NM_ID))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.product.type"));
				}
			}
			
			if(!Util.isNotNull(record.get(ProductFrameEnum.DT_EXPIRATION.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.dt.expiration"));
			}
			else {
				try {
					if (!Util.isAfterToday(record.get(ProductFrameEnum.DT_EXPIRATION.getName()).toString())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.ERROR_DT_BEFORE_TODAY, "lbl.dt.expiration"));
					}
				}
				catch(ParseException pae) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FORMAT_FIELD, "lbl.dt.expiration"));
				}
			}
			
			if(!Util.isNotNull(record.get(ProductFrameEnum.VALUE.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.product.value"));
			}
			else if (record.get(ProductFrameEnum.VALUE.getName()).toString().length() > ProductFrameEnum.VALUE.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.product.value", 
						ProductFrameEnum.VALUE.getLength().toString()));
			}
			
			if(!Util.isNotNull(record.get(ProductFrameEnum.AMOUNT.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.amount"));
			}
			else if (record.get(ProductFrameEnum.AMOUNT.getName()).toString().length() > ProductFrameEnum.AMOUNT.getLength()) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, "lbl.amount", 
						ProductFrameEnum.AMOUNT.getLength().toString()));
			}

			if(!Util.isNotNull(record.get(ProductFrameEnum.STORE.getName()))) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.store"));
			}
			else {
				Map<String, Object> mapa = (Collections.synchronizedMap((LinkedHashMap)record.get(ProductFrameEnum.STORE.getName())));
				if(!Util.isNotNull(mapa.get(Pojo.NM_ID))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.store"));
				}
			}
		}
		return arrays;
	}*/
	
}
