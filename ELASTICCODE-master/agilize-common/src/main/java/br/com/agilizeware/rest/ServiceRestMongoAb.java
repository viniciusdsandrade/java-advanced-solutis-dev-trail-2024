package br.com.agilizeware.rest;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.agilizeware.dao.MongoDaoAB;
import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.dto.Pojo;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;

public abstract class ServiceRestMongoAb extends ServiceRestAb {
	
	protected abstract MongoDaoAB definirMongoDao();
	
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<Pojo> save(@RequestBody Pojo record) {
		RestResultDto<Pojo> result = new RestResultDto<Pojo>();
		result.setData(definirMongoDao().save(record));
		result.setSuccess(true);
		return result;
	}

	//@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{id}/{collection}", method = RequestMethod.PUT)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<String> update(@PathVariable("id") String id, @PathVariable("collection") String collection,
    		/*@RequestBody String record*/ @RequestBody Map<String, Object> record) throws JsonProcessingException {
		
		RestResultDto<String> result = new RestResultDto<String>();
		//try {
			//Map<String, Object> mapa = new ObjectMapper().readValue(record, HashMap.class);
			definirMongoDao().update(id, collection, record);
			result.setData(id);
			result.setSuccess(true);
		/*}catch(IOException e) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getMapper().writeValueAsString(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, e)));
		}*/
		return result;
	}
	
	@RequestMapping(value = "/{id}/{collection}", method = RequestMethod.DELETE)
	@ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	public RestResultDto<String> delete(@PathVariable("id") String id, @PathVariable("collection") String collection) {
		
		if(!Util.isNotNull(id) || !Util.isNotNull(collection)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk");
		}
		definirMongoDao().delete(id, collection);
		RestResultDto<String> result = new RestResultDto<String>();
		result.setSuccess(true);
		result.setData(id);
		return result;
	}
	
	@RequestMapping(value = "/{id}/{collection}", method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto<Pojo> findOne(@PathVariable("id") String id, @PathVariable("collection") String collection) {
		
		if(!Util.isNotNull(id) || !Util.isNotNull(collection)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk");
		}
		
		Pojo p = definirMongoDao().findOne(id, collection);
		RestResultDto<Pojo> result = new RestResultDto<Pojo>();
		result.setSuccess(true);
		result.setData(p);
		return result;
	}

	@RequestMapping(value = "/filter/{collection}", method = RequestMethod.GET)
    @ResponseBody
    public RestResultDto<PageableListDTO<Pojo>> findByFilter(@PathVariable(required = true, name="collection") String collection,
    		@RequestParam(required = true, name="filter") String filter) throws JsonProcessingException {
		
		RestResultDto<PageableListDTO<Pojo>> result = new RestResultDto<PageableListDTO<Pojo>>();
		try {
			PageableFilterDTO filterDto = RestResultDto.getMapper().readValue(filter, PageableFilterDTO.class);
			result.setData(definirMongoDao().findPageable(collection, filterDto));
			result.setSuccess(true);
		}
		catch(IOException ioe) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe)));
		}		
		return result;
	}
}
