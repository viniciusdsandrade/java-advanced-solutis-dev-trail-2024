package br.com.agilizeware.dto;

public class PayloadDto extends DtoAb {

	private static final long serialVersionUID = 1901154022833034482L;
	private String clientId;
	private String redirectUri;
	private String code;
	//[state, code, authuser, hd, session_state, prompt]
	public PayloadDto() {
		super();
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getRedirectUri() {
		return redirectUri;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
