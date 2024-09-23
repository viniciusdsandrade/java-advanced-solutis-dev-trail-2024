package br.com.agilizeware.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value={"hibernateLazyInitializer", "handler"})
public interface EntityIf extends Serializable {
	
	public abstract Long getId(); 
	public abstract void setId(Long id);
	public abstract Date getDtCreate();
	public abstract void setDtCreate(Date dtCreate);
	public abstract Long getIdUserCreate();
	public abstract void setIdUserCreate(Long idUserCreate);
	public abstract String getLabel(); 
	
}
