package br.com.agilizeware.isobrou.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import br.com.agilizeware.dto.Pojo;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ApplicationNamesEnum;
import br.com.agilizeware.enums.EmailFrameEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.OrderQueueEnum;
import br.com.agilizeware.enums.QueueEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.isobrou.ServiceAuthenticationFilter;
import br.com.agilizeware.isobrou.model.AddressFrameEnum;
import br.com.agilizeware.isobrou.model.StoreFrameEnum;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.File;
import br.com.agilizeware.model.Queue;
import br.com.agilizeware.rest.ServiceRestAb;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/store")
@Service
public class StoreServiceRest extends ServiceRestAb{

		@Autowired
		private AppPropertiesService appPropertiesService;
	    
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@RequestMapping(method = RequestMethod.POST)
	    @ResponseBody
		//@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	    public RestResultDto<Pojo> saveStore(@RequestBody Map<String, Object> store) throws JsonProcessingException {
		
			RestResultDto<Pojo> result = new RestResultDto<Pojo>();
			try {
				
				//Validando os dados do Estabelecimento a ser salvo
				List<AgilizeException> errors = validateMapOperations(store, StoreFrameEnum.values());
				if(Util.isListNotNull(errors)) {
					result.setSuccess(false);
					result.setStrAgilizeExceptionError(RestResultDto.getStrException(
							new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD, errors)));
					return result;
				}
				
				//Salvando a imagem do estabelecimento recebido
				String nmFileServer = ApplicationNamesEnum.FILESERVER.getName();
				String tokenCaller = ServiceAuthenticationFilter.NAME_APP + ":" + ServiceAuthenticationFilter.KEY;
				Application appFileServer = null;
				List<File> fileSaved = null;
				if(Util.isNotNull(store.get(StoreFrameEnum.FILE.getName()))) {
					appFileServer = Util.getApplication(nmFileServer, tokenCaller);
					String urlFile = appFileServer.getHost() + appPropertiesService.getPropertyString("path.file.save.temp.file.service");
					
					List<File> receiveds = RestResultDto.getMapper().convertValue(
							store.get(StoreFrameEnum.FILE.getName()), new TypeReference<List<File>>() {});
					Object obj = Util.accessRestService(urlFile, 2, receiveds, 
							RestResultDto.class, appFileServer.getPassword(), null);
					if(Util.isNotNull(obj)) {
						fileSaved =  (List<File>)obj;
						store.put(StoreFrameEnum.FILE.getName(), RestResultDto.getMapper().writeValueAsString(
								fileSaved.get(0)));
					}
				}
				
				//Instanciando um WF
				//TODO: Corrigir o erro de deployment do fluxo bpmn e publicar o agilize-flow no heroku.
				/*Application appWF = Util.getApplication(ApplicationNamesEnum.WORKFLOW.getName(), 
						tokenCaller);
				Map<String, Object> mapFlow = new HashMap<>(1);
				mapFlow.put(Pojo.NM_ENTITY, StoreFrameEnum.COLLECTION.getName());
				mapFlow.put(StoreFrameEnum.COMPANY_NAME.getName(), store.get(StoreFrameEnum.COMPANY_NAME.getName()));
				
				String urlWF = appWF.getHost() + appPropertiesService.getPropertyString("path.flow.init.instance") + 
						WorkFlowEnum.ISOBROU.getId();
				Object objFlow = Util.accessRestService(urlWF, 2, mapFlow, String.class, appWF.getPassword(), null);
				if(Util.isNotNull(objFlow)) {
					store.put(StoreFrameEnum.INSTANCE_FLOW.getName(), objFlow.toString());
				}*/
				
				//Salvando o documento do Estabelecimento
				Application appElastic = Util.getApplication(ApplicationNamesEnum.ELASTICCODE.getName(), 
						tokenCaller);
				String url = appElastic.getHost() + appPropertiesService.getPropertyString("path.mongo.pojo");
				
				Application appIsobrou = Util.getApplication(ServiceAuthenticationFilter.NAME_APP, tokenCaller);
				store.put(StoreFrameEnum.APPLICATION.getName(), appIsobrou.getId());
				
				String companyName = store.get(StoreFrameEnum.COMPANY_NAME.getName()).toString();
				Pojo pojo = new Pojo();
				pojo.setSoundex(Pojo.strSoundex(companyName));  
				pojo.setNmEntity(StoreFrameEnum.COLLECTION.getName());
				pojo.setCollection(StoreFrameEnum.COLLECTION.getName());
				pojo.setjSon(RestResultDto.getMapper().writeValueAsString(store));
				//Incluindo o Estabelecimento
				Object objStore = Util.accessRestService(url, 2, pojo, RestResultDto.class, appElastic.getPassword(), null);
				if(Util.isNotNull(objStore)) {
					pojo = RestResultDto.getMapper().convertValue(objStore, new TypeReference<Pojo>() {});
				}
				
				//Mandando email para o Representante do Estabelecimento
				Queue q = new Queue();
				q.setIdUserCreate(appPropertiesService.getPropertyLong("id.user.administrator"));
				q.setNmEntity(StoreFrameEnum.COLLECTION.getName());
				q.setApplication(appIsobrou);
				q.setQueue(QueueEnum.MAIL);
				q.setPriority(OrderQueueEnum.MINIMAL);
				q.setHistoric(false);
				
				Map<String, Object> address = RestResultDto.getMapper().convertValue(
						store.get(StoreFrameEnum.ADDRESS.getName()), new TypeReference<HashMap>() {});
				String email = address.get(AddressFrameEnum.EMAIL.getName()).toString();
				String sponsor = store.get(StoreFrameEnum.SPONSOR.getName()).toString();
				
				Map<String, Object> mapParameters = new HashMap<>();
				mapParameters.put(EmailFrameEnum.ADDRESS.getName(), email);
				mapParameters.put(EmailFrameEnum.SUBJECT.getName(), appPropertiesService.getPropertyString("email.text.subject.save.store"));
				mapParameters.put(EmailFrameEnum.TEMPLATE_EXTERNAL.getName(), 
						appPropertiesService.getPropertyString("id.file.template.email.save.store"));
				
				Map<String, Object> param = new HashMap<>();
				param.put(StoreFrameEnum.SPONSOR.getName(), sponsor);
				param.put(StoreFrameEnum.COMPANY_NAME.getName(), companyName);
				
				mapParameters.put(EmailFrameEnum.PARAMETERS.getName(), param);
				q.setMapParameters(mapParameters);
				//TODO: Testar e Corrigir o envio de email com template externo.
				/*Application appAssync = Util.getApplication(ApplicationNamesEnum.ASSYNCRONO.getName(), 
						tokenCaller);
				String urlAssync = appAssync.getHost() + appPropertiesService.getPropertyString("path.assync.start");
				Util.accessRestService(urlAssync, 2, q, RestResultDto.class, appAssync.getPassword(), null);*/
				
				result.setSuccess(true);
				result.setData(pojo);

			} catch(IOException e) {
				result.setSuccess(false);
				result.setStrAgilizeExceptionError(RestResultDto.getMapper().writeValueAsString(
						new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, e)));
				return result;
			}	
			
			return result;
		}
		
}