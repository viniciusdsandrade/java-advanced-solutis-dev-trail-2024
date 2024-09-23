package br.com.agilizeware.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
//@Document
public abstract class DtoAb implements Serializable {

	private static final long serialVersionUID = 6969526853419628334L;
	
	/*@Id
	private String id;
	@Field
    private String nmEntity;
	@Field
    private Date dtCreate;
	@Field
    private String soundex;

	public void setNmEntity(String nmEntity) {
		this.nmEntity = nmEntity;
	}
	
	public abstract String getNmEntity();	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDtCreate() {
		return dtCreate;
	}

	public void setDtCreate(Date dtCreate) {
		this.dtCreate = dtCreate;
	}

	public String getSoundex() {
		return soundex;
	}

	public void setSoundex(String soundex) {
		if(soundex != null && !soundex.isEmpty()) {
			this.soundex = (new Soundex()).encode(soundex);
		}
	}*/
}
