package br.com.agilizeware.enums;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@SuppressWarnings("hiding")
@JsonSerialize(using = EnumSerializer.class)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IEnum<Integer> {

	Integer getId();
	String getDescription();
	String getLabel();
	String toString();
}
