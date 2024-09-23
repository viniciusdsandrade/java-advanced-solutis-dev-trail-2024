package br.com.agilizeware.security.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ApplicationNamesEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.File;
import br.com.agilizeware.model.Function;
import br.com.agilizeware.model.User;
import br.com.agilizeware.rest.ServiceRestEntityAb;
import br.com.agilizeware.security.repository.SecurityDaoImpl;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@RestController
@RequestMapping("/agilize")
public class FacadeServiceRest extends ServiceRestEntityAb<Function, Long> {

	@Autowired
	private SecurityDaoImpl securityDaoIf;
	@Autowired
	private AppPropertiesService appPropertiesService;

	
	@Override
	protected DaoAB<Function, Long> definirDao() {
		return securityDaoIf;
	}
	
	@RequestMapping(value="/facade", method = RequestMethod.GET)
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RestResultDto doGet(@RequestParam(value="parameters", required=false) String parameters, HttpServletRequest request) {
		
		String path = request.getHeader(Util.HEADER_PATH_KEY); 
		String aplication = request.getHeader(Util.HEADER_APPLICATION_KEY); 
		
		Application app = securityDaoIf.findCompleteApplicationByName(aplication);
		
		if(!Util.isNotNull(app) || !Util.isNotNull(app.getId())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.APPLICATION_NOT_FOUND);
		}

		String url = app.getHost() + "/" + path;
		Object access = null;
		//access = Util.accessRestService(url, 5, objJson, RestResultDto.class, app.getPassword(), parameters);
		Map<String, String> map = new HashMap<String, String>(1);
		map.put("parameters", parameters);
		access = Util.accessRestService(url, 5, null, RestResultDto.class, app.getPassword(), map);
		
		RestResultDto result = new RestResultDto();
		result.setData(access);
		result.setSuccess(true);
		return result;
	}
	
	@RequestMapping(value="/facade/filter", method = RequestMethod.GET)
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RestResultDto doGetFilter(@RequestParam(value = "filters", required = false) String filters, HttpServletRequest request) {
		
		String path = request.getHeader(Util.HEADER_PATH_KEY); 
		String aplication = request.getHeader(Util.HEADER_APPLICATION_KEY); 
		
		Application app = securityDaoIf.findCompleteApplicationByName(aplication);
		
		if(!Util.isNotNull(app) || !Util.isNotNull(app.getId())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.APPLICATION_NOT_FOUND);
		}

		String url = app.getHost() +"/"+ path;
		Object access = null;
		
		Map<String, String> map = new HashMap<String, String>(1);
		map.put("filters", filters);
		access = Util.accessRestService(url, 5, null, RestResultDto.class, app.getPassword(), map);
		
		RestResultDto result = new RestResultDto();
		result.setData(access);
		result.setSuccess(true);
		return result;
	}
	
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/facade/enums", method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto doGetEnum(@RequestParam(value = "nameClassEnum", required = true) String nameClassEnum, HttpServletRequest request) {
		
		RestResultDto result = new RestResultDto();
		try {
			Class classEnum = Class.forName(nameClassEnum); 
			Object[] objs = classEnum.getEnumConstants();
			result.setSuccess(true);
			result.setData(objs);
		}catch(ClassNotFoundException cnfe) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ENUM_NOT_FOUND, cnfe, nameClassEnum)));
		}
		return result;
	}*/
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/facade", method = RequestMethod.POST)
	@ResponseBody
	public RestResultDto doPost(@RequestParam(name="parameters", required=false) Map parameters, 
			@RequestBody(required=false) String objJson, HttpServletRequest request) {
		
		RestResultDto result = new RestResultDto();
		
		String path = request.getHeader(Util.HEADER_PATH_KEY); 
		String aplication = request.getHeader(Util.HEADER_APPLICATION_KEY); 
		
		Application app = securityDaoIf.findCompleteApplicationByName(aplication);
		
		if(!Util.isNotNull(app) || !Util.isNotNull(app.getId())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.APPLICATION_NOT_FOUND);
		}

		String url = app.getHost() + "/" + path;
		
		if(!"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
			User us = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(parameters == null) {
				parameters = new HashMap<String, Long>(1);
			}
			parameters.put("idUser", us.getId());
		}
	
		Object access = null;
		access = Util.accessRestService(url, 2, objJson, RestResultDto.class, app.getPassword(), parameters);
		
		result.setData(access);
		result.setSuccess(true);
		return result;
	}
	
	@RequestMapping(value="/facade", method = RequestMethod.PUT)
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RestResultDto doPut(@RequestParam(name="parameters", required=false) Map<String, Object> parameters, 
			@RequestBody(required=false) String objJson, HttpServletRequest request) {
		
		String path = request.getHeader(Util.HEADER_PATH_KEY); 
		String aplication = request.getHeader(Util.HEADER_APPLICATION_KEY); 
		
		Application app = securityDaoIf.findCompleteApplicationByName(aplication);
		
		if(!Util.isNotNull(app) || !Util.isNotNull(app.getId())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.APPLICATION_NOT_FOUND);
		}
		
		if(!"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
			User us = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(parameters == null) {
				parameters = new HashMap<String, Object>(1);
			}
			parameters.put("idUser", us.getId());
		}


		String url = app.getHost() + "/" + path;
		Object access = null;
		access = Util.accessRestService(url, 3, objJson, RestResultDto.class, app.getPassword(), parameters);
		
		RestResultDto result = new RestResultDto();
		result.setData(access);
		result.setSuccess(true);
		return result;
	}
	
	@RequestMapping(value="/facade", method = RequestMethod.DELETE)
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RestResultDto doDelete(@RequestParam(name="parameters", required=false) Map<String, Object> parameters, 
			@RequestBody(required=false) String objJson, HttpServletRequest request) {
		
		String path = request.getHeader(Util.HEADER_PATH_KEY); 
		String aplication = request.getHeader(Util.HEADER_APPLICATION_KEY); 
		
		Application app = securityDaoIf.findCompleteApplicationByName(aplication);
		
		if(!Util.isNotNull(app) || !Util.isNotNull(app.getId())) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.APPLICATION_NOT_FOUND);
		}
		
		if(!"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
			User us = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(parameters == null) {
				parameters = new HashMap<String, Object>(1);
			}
			parameters.put("idUser", us.getId());
		}


		String url = app.getHost() + "/" +  path;
		Object access = null;
		access = Util.accessRestService(url, 4, objJson, RestResultDto.class, app.getPassword(), parameters);
		
		RestResultDto result = new RestResultDto();
		result.setData(access);
		result.setSuccess(true);
		return result;
	}
	
	
	@RequestMapping(value="/facade/applications", method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto<List<Application>> findApplications(HttpServletRequest request) {
		
		List<Application> apps = securityDaoIf.findApplications(true);
		RestResultDto<List<Application>> result = new RestResultDto<List<Application>>();
		result.setSuccess(true);
		result.setData(apps);
		return result;
	}
	

	@RequestMapping(value="/facade/application", method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto<Application> findCompleteApplicationByName(
			@RequestParam(value = "nmApplication", required = true) String nmApplication, 
			@RequestParam(value = "isInternal", required = false) Boolean isInternal, 
			HttpServletRequest request) {
		
		Application app = securityDaoIf.findCompleteApplicationByName(nmApplication);
		
		//Pegando informações da imagem de fundo
		String nmFileServer = ApplicationNamesEnum.FILESERVER.getName();
		if(Util.isNotNull(app.getIdBackGround()) && !nmFileServer.equals(nmApplication)) {
			String tokenCaller = app.getName() + ":" + app.getPassword();
			Application appFileServer = Util.getApplication(nmFileServer, tokenCaller);
			String urlFile = appFileServer.getHost() + appPropertiesService.getPropertyString("path.file.find.by.id");
			urlFile = urlFile + app.getIdBackGround();
			Object obj = Util.accessRestService(urlFile, 5, null, RestResultDto.class, appFileServer.getPassword(), null);
			File file = null;
			if(Util.isNotNull(obj)) {
				file = RestResultDto.getMapper().convertValue(obj, new TypeReference<File>() {});
			}
			app.setBackgroundImage(file);
		}
		
		if(!Util.isNotNull(isInternal) || !isInternal) {
			app.setPassword(null);
		}

		RestResultDto<Application> result = new RestResultDto<Application>();
		result.setSuccess(true);
		result.setData(app);
		return result;
	}

}
