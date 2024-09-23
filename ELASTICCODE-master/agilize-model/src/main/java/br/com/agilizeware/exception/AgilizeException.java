package br.com.agilizeware.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.agilizeware.dto.RestErrorDto;
import br.com.agilizeware.enums.IEnum;

@JsonDeserialize
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class", defaultImpl=AgilizeException.class)
public class AgilizeException extends RuntimeException {
	
	private static final long serialVersionUID = 8782674655840347611L;
	
	private Integer httpStatus;
	private Integer errorCode;
	private String debugMessage;
	private String i18nKey;
	private String[] params;
	@JsonIgnore
	private Throwable exception;
	private IEnum<Integer> errorCodeEnum;
	private List<AgilizeException> erros;
	private RestErrorDto errorDto;
	
	public AgilizeException() {}
	
	public AgilizeException(RestErrorDto errorDto) {
		this.errorDto = errorDto;
	}

	public AgilizeException(Integer httpStatus, IEnum<Integer> errorCodeEnum, String... params) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCodeEnum.getId();
		this.debugMessage = errorCodeEnum.getDescription();
		this.i18nKey = errorCodeEnum.getLabel();
		this.params = params;
		this.errorCodeEnum = errorCodeEnum;
	}
	
	public AgilizeException(Integer httpStatus, IEnum<Integer> errorCodeEnum, Throwable original, String... params) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCodeEnum.getId();
		this.debugMessage = errorCodeEnum.getDescription();
		this.i18nKey = errorCodeEnum.getLabel();
		this.params = params;
		this.errorCodeEnum = errorCodeEnum;
		this.exception = original;
	}

	public AgilizeException(Integer httpStatus, Integer errorCode, String debugMessage, String i18nKey,String... params) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.debugMessage = debugMessage;
		this.i18nKey = i18nKey;
		this.params = params;
	}
	
	public AgilizeException(Integer httpStatus, Integer errorCode, String debugMessage, String i18nKey, Throwable exception, String... params) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.debugMessage = debugMessage;
		this.i18nKey = i18nKey;
		this.params = params;
		this.exception = exception;
	}
	
	public AgilizeException(Integer httpStatus, IEnum<Integer> errorCodeEnum, List<AgilizeException> erros) {
		this.httpStatus = httpStatus;
		this.errorCode = errorCodeEnum.getId();
		this.debugMessage = errorCodeEnum.getDescription();
		this.i18nKey = errorCodeEnum.getLabel();
		this.errorCodeEnum = errorCodeEnum;		
		this.erros = erros;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public String getI18nKey() {
		return i18nKey;
	}

	public String[] getParams() {
		return params;
	}

	public Throwable getException() {
		return exception;
	}

	public IEnum<Integer> getErrorCodeEnum() {
		return errorCodeEnum;
	}

	public List<AgilizeException> getErros() {
		return erros;
	}

	public RestErrorDto getErrorDto() {
		return errorDto;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		if(debugMessage != null && !debugMessage.isEmpty()) {
			return debugMessage;
		}
		else if(errorDto != null) {
			return errorDto.getDebugMessage();
		}
		return "Mensagem Não Definida";
	}
	
	
}
