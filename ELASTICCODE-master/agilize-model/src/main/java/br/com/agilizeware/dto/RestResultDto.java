package br.com.agilizeware.dto;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;

@JsonSerialize
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RestResultDto<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private T data;
	private Boolean success;
	private String strAgilizeExceptionError;
	
	public RestResultDto() {
		super();
	}
	
	public static ObjectMapper getMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true );
	    objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    objectMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	    objectMapper.configure( Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	    objectMapper.configure( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	    
	    /*objectMapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	    objectMapper.configure( DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
	    objectMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, false);*/
	    
	    objectMapper.setVisibility( PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY );
	    //objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
	    //objectMapper.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY );
	    
	    return objectMapper;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	@JsonIgnore
	public AgilizeException getError() {
		if(strAgilizeExceptionError != null && !strAgilizeExceptionError.isEmpty()) {
			try {
				return getMapper().readValue( strAgilizeExceptionError, AgilizeException.class );
			}
			catch(JsonParseException jpe) {
				throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, jpe);
			}
			catch(JsonMappingException jme) {
				throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, jme);
			}
			catch(IOException ioe) {
				throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_JSON, ioe);
			}
		}
		return null;
	}

	public String getStrAgilizeExceptionError() {
		return strAgilizeExceptionError;
	}

	public void setStrAgilizeExceptionError(String strAgilizeExceptionError) {
		this.strAgilizeExceptionError = strAgilizeExceptionError;
	}
	
	public T getValue(TypeReference<T> typeRef) {
		if(this.getData() != null && !this.getData().toString().equals("")) {
			return getMapper().convertValue(this.getData(), typeRef);
		}
		return null;
	}
	
	public String getNmEntity() {
		return null;
	}
	
	@JsonIgnore
	public static String getStrException(Exception e) {
		try {
			return getMapper().writeValueAsString(e);
		}
		catch(JsonProcessingException jpe) {
			throw new AgilizeException(HttpStatus.SEE_OTHER.ordinal(), ErrorCodeEnum.ERROR_JSON, jpe);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@JsonIgnore
	public static Map<String, Object> getMap(String json) {
		Map<String, Object> ret = null;
		try {
			ret = RestResultDto.getMapper().readValue(json, HashMap.class);
		}
		catch(IOException ioe) {
			ret = RestResultDto.getMapper().convertValue(json, new TypeReference<HashMap>() {});
		}
		return ret;
	}
}
