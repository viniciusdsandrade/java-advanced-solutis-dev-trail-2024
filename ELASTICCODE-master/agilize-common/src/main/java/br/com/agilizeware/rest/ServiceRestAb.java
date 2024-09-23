package br.com.agilizeware.rest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.MongoTimeoutException;

import br.com.agilizeware.dto.RestErrorDto;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.IFrameEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;

@Service
public abstract class ServiceRestAb {
	
	protected static final Logger log = LogManager.getLogger(ServiceRestAb.class);
	
	@ExceptionHandler(Throwable.class)
	//@ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody RestResultDto<RestErrorDto> handleAllException(Throwable th, HttpServletResponse response) {

		log.error(th.getMessage(), th);
		
		RestResultDto<RestErrorDto> result = new RestResultDto<RestErrorDto>();
		result.setSuccess(false);
		
        AgilizeException ab;
        if (th instanceof AgilizeException) {
            ab = (AgilizeException)th;
        }
        else if(th instanceof JsonProcessingException || (th.getCause() != null && th.getCause() instanceof JsonProcessingException)) {
        	ab = new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_JSON);
        }
        else if(th instanceof ConnectException || th instanceof ResourceAccessException || (th.getCause() != null && (th.getCause() instanceof ConnectException
        		|| th.getCause() instanceof ResourceAccessException))) {
        	ab = new AgilizeException(HttpStatus.NOT_FOUND.ordinal(), ErrorCodeEnum.ERROR_CONECTION, th.getMessage());
        }
        else if(th instanceof MongoTimeoutException || (th.getCause() != null && th.getCause() instanceof MongoTimeoutException)) {
        	ab = new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_MONGO_CONECTION, th.getMessage());
        }
        else {
        	ab = new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.DEFAULT_EXCEPTION);
        }

        RestErrorDto restError = null;
        if(Util.isNotNull(ab.getErrorDto())) {
        	restError = ab.getErrorDto();
        }
        else {
            restError = new RestErrorDto();
            restError.setDebugMessage(ab.getDebugMessage());
        	restError.setErrorCode(ab.getErrorCode());
        	restError.setHttpStatus(ab.getHttpStatus());
            restError.setI18nKey(ab.getI18nKey());
            restError.setParams(ab.getParams());
        	
        	if(Util.isListNotNull(ab.getErros())) {
            	RestErrorDto error = null;
            	restError.setErrors(new ArrayList<RestErrorDto>(1));

            	for(AgilizeException abs : ab.getErros()) {
            		error = new RestErrorDto();
            		error.setDebugMessage(abs.getDebugMessage());
            		error.setErrorCode(abs.getErrorCode());
            		error.setHttpStatus(abs.getHttpStatus());
            		error.setI18nKey(abs.getI18nKey());
            		error.setParams(abs.getParams());
            		
            		restError.getErrors().add(error);
            	}
            }
        }
                
        log.error("Error status: ["+restError.getHttpStatus()+"] code:["+restError.getErrorCode()+"] Message: ["+restError.getDebugMessage()+"] StackTrace >>>");
        if(Util.isNotNull(ab.getException())){
            log.error(ab.getException());
            ab.getException().printStackTrace();
        }else {
            log.error(th);
            th.printStackTrace();
        }
        
        result.setData(restError);
        return result;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<AgilizeException> validateMapOperations(Map<String, Object> filter, IFrameEnum[] enums) {
		
		List<AgilizeException> arrays = new ArrayList<AgilizeException>(1);
		IFrameEnum[] norOperations = null;
		for(IFrameEnum frame : enums) {
			
			if(!frame.isNotValidate()) {
				//Nulidade
				if(frame.isRequired() && (!Util.isNotNull(filter.get(frame.getName())) || 
						(frame.isNumeric() && !Util.isNotNull(Util.onlyNumbers(filter.get(frame.getName()).toString()))))) {
					arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, frame.getLabel()));
				}
				
				if(Util.isNotNull(filter.get(frame.getName())) && !Util.isNotNull(frame.getDaughters())) {
					//Tamanho do Campo
					if(Util.isNotNull(frame.getLength())) {
						if(frame.getOperLength().equals(IFrameEnum.LTE) && 
								filter.get(frame.getName()).toString().length() > frame.getLength()) {
							arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, frame.getLabel(), 
									frame.getLength().toString()));
						}
						else if(frame.getOperLength().equals(IFrameEnum.EQ) && 
								filter.get(frame.getName()).toString().length() != frame.getLength()) {
							arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, frame.getLabel(), 
									frame.getLength().toString()));
						}
						else if(frame.getOperLength().equals(IFrameEnum.GTE) && 
								filter.get(frame.getName()).toString().length() < frame.getLength()) {
							arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_LENGTH_FIELD, frame.getLabel(), 
									frame.getLength().toString()));
						}
					}
					
					//Enum
					if(Util.isNotNull(frame.getClassEnum())) {
						try {
							Class classEnum = Class.forName(frame.getClassEnum()); 
							classEnum.getMethod("findByCode").invoke(null, Integer.valueOf(Util.onlyNumbers(filter.get(frame.getName()).toString())));
						}
						catch(ArrayIndexOutOfBoundsException | ClassNotFoundException | NoSuchMethodException | 
								InvocationTargetException | IllegalAccessException e) {
							arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, e, frame.getLabel()));
						}
					}
					
					//Data
					if(Util.isNotNull(frame.getPatternDate())) {
					
						if(frame.getPatternDate().equals(IFrameEnum.DTHOUR)) {
							try {
								Util.getDateWhitHMS(filter.get(frame.getName()).toString());
							}
							catch(RuntimeException re) {
								arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, re, frame.getLabel()));
							}
						}
						else {
							try {
								Util.getDateWhitoutHMS(filter.get(frame.getName()).toString());
							}
							catch(RuntimeException re) {
								arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, re, frame.getLabel()));
							}
						}
					}
					
					//Regex
					if(Util.isNotNull(frame.regex()) && !Util.isValid(frame.regex(), filter.get(frame.getName()).toString())) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.INVALID_FIELD, frame.getLabel()));
					}
				}
				
				//Validando campos que ao menos um é obrigatório
				if(!Util.isNotNull(norOperations) && Util.isNotNull(frame.getNorOperations())) {
					norOperations = frame.getNorOperations();
				}
				
				//Objeto dentro de Objeto
				if(Util.isNotNull(frame.getDaughters())) {
					if(Util.isNotNull(filter.get(frame.getName()))) {
						if(frame.isList()) {
							ArrayList lista = RestResultDto.getMapper().convertValue(filter.get(frame.getName()), 
									new TypeReference<ArrayList>() {});
							Iterator it = lista.iterator();
							while(it.hasNext()) {
								Map<String, Object> map = RestResultDto.getMapper().convertValue(it.next(), 
										new TypeReference<HashMap>() {});
								arrays.addAll(validateMapOperations(map, frame.getDaughters()));
							}
							
						}
						else {
							try {
								
								Map<String, Object> map = null;
								try {
									map = RestResultDto.getMapper().readValue(filter.get(frame.getName()).toString(), 
											new TypeReference<HashMap>() {});
								}
								catch(JsonParseException jpe) {
									map = RestResultDto.getMapper().convertValue(filter.get(frame.getName()), 
											new TypeReference<HashMap>() {});
								}
								arrays.addAll(validateMapOperations(map, frame.getDaughters()));
							}
							catch(IOException ioe) {
								throw new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe);
							}
						}
					}
					else if(frame.isRequired()) {
						arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIRED_FIELD, frame.getLabel()));
					}
				}				
			}
		}
		
		//Validando campos que ao menos um é obrigatório
		if(Util.isNotNull(norOperations)) {
			int nullField = 0;
			String[] labels = new String[norOperations.length];
			int aux = 0;
			for(IFrameEnum iFrame : norOperations) {
				if(!Util.isNotNull(filter.get(iFrame.getName())) || 
						(filter.get(iFrame.getName()) instanceof Boolean && (!(Boolean)filter.get(iFrame.getName())))) {
					nullField++;
				}
				labels[aux] = iFrame.getLabel();
				aux++;
			}
			if(nullField == norOperations.length) {
				arrays.add(new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.ERROR_NOR_FIELDS, labels));
			}
		}
		
		return arrays;
	}
}
