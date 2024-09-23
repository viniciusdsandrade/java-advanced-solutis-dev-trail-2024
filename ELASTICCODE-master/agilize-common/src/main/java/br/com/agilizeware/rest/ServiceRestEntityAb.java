package br.com.agilizeware.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.dto.PageableFilterDTO;
import br.com.agilizeware.dto.PageableListDTO;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.EntityIf;
import br.com.agilizeware.util.Util;

public abstract class ServiceRestEntityAb<E extends EntityIf, ID extends Serializable> extends ServiceRestAb {
	
	protected abstract DaoAB<E, ID> definirDao();

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto<E> findOne(@PathVariable("id") ID id) {
		
		if(!Util.isNotNull(id)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk");
		}
		
		E entity = definirDao().findOne(id);
		RestResultDto<E> result = new RestResultDto<E>();
		result.setSuccess(true);
		result.setData(entity);
		return result;
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	public RestResultDto<List<E>> findAll() {
		
		List<E> listE = definirDao().findAll();
		RestResultDto<List<E>> result = new RestResultDto<List<E>>();
		result.setSuccess(true);
		result.setData(listE);
		return result;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
	public RestResultDto<ID> delete(@PathVariable("id") ID id) {
		
		if(!Util.isNotNull(id)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.pk");
		}
		definirDao().delete(id);
		RestResultDto<ID> result = new RestResultDto<ID>();
		result.setSuccess(true);
		result.setData(id);
		return result;
	}
	
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ResponseBody
    /*public RestResultDto<PageableListDTO<E>> findByFilter(
    		@RequestParam(required = true, name="filter") String filter) {*/
    public RestResultDto<PageableListDTO<E>> findByFilter(@RequestParam(value = "filters", required = false) String filter, HttpServletRequest request) {
		
		PageableFilterDTO pageFilter = null;
		PageableListDTO<E> obj = null;
		RestResultDto<PageableListDTO<E>> result = new RestResultDto<PageableListDTO<E>>();
		try {
			pageFilter = RestResultDto.getMapper().readValue(filter, PageableFilterDTO.class);
			obj = definirDao().findPageable(pageFilter);
			result.setSuccess(true);
			result.setData(obj);
		}
		catch(IOException jpe) {
			result.setSuccess(false);
			result.setStrAgilizeExceptionError(RestResultDto.getStrException(
					new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, jpe)));
		}
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public RestResultDto<E> save(@RequestBody E record) {
		if(!Util.isNotNull(record)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, "lbl.record");
		}
		definirDao().save(record);
		RestResultDto<E> result = new RestResultDto<E>();
		result.setSuccess(true);
		result.setData(record);
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	}

}
