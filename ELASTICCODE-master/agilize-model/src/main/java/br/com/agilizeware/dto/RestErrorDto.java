package br.com.agilizeware.dto;

import java.util.List;

public class RestErrorDto extends DtoAb {
	
	private static final long serialVersionUID = 220106053406571435L;

	private Integer httpStatus;
	private Integer errorCode;
	private String debugMessage;
	private String i18nKey;
	private String[] params; 
	private List<RestErrorDto> errors;
	
	public RestErrorDto() {
		super();
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public String getI18nKey() {
		return i18nKey;
	}

	public void setI18nKey(String i18nKey) {
		this.i18nKey = i18nKey;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public List<RestErrorDto> getErrors() {
		return errors;
	}

	public void setErrors(List<RestErrorDto> errors) {
		this.errors = errors;
	}
	
	public String getNmEntity() {
		return null;
	}
}
