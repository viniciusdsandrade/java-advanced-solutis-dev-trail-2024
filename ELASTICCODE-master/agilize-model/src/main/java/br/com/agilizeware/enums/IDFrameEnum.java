package br.com.agilizeware.enums;

import br.com.agilizeware.enums.IFrameEnum;

public enum IDFrameEnum implements IFrameEnum {

	ID("id", 12, IFrameEnum.LTE, true, "lbl.id", true, null, null, null, null, null, null), 
	;
	
	private String name;
	private Integer length;
	private String operLength;
	private Boolean required;
	private String label;
	private Boolean numeric;
	private String classEnum;
	private String patternDate;
	private String regex;
	private IFrameEnum[] daughters;
	private Boolean notValidate;
	private Boolean isList;
	
	private IDFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
			Boolean numeric, String classEnum, String patternDate, String regex, IFrameEnum[] daughters, Boolean notValidate, Boolean isList) {
		this.name = name;
		this.length = length;
		this.required = required;
		this.label = label;
		this.operLength = operLength;
		this.numeric = numeric;
		this.classEnum = classEnum;
		this.patternDate = patternDate;
		this.regex = regex;
		this.daughters = daughters;
		this.notValidate = notValidate;
		this.isList = isList;
	}

	public Boolean isList() {
		if(isList == null) {
			return false;
		}
		return isList;
	}

	public String getName() {
		return this.name;
	}

	public Integer getLength() {
		return length;
	}

	public String getOperLength() {
		return operLength;
	}

	public Boolean isRequired() {
		if (required == null) {
			return false;
		}
		return required;
	}

	public String getLabel() {
		return label;
	}

	public Boolean isNumeric() {
		if (numeric == null) {
			return false;
		}
		return numeric;
	}

	public String getClassEnum() {
		return classEnum;
	}

	public String getPatternDate() {
		return patternDate;
	}

	public String regex() {
		return regex;
	}

	public IFrameEnum[] getDaughters() {
		return daughters;
	}

	public IFrameEnum[] getNorOperations() {
		return null;
	}
	
	public Boolean isNotValidate() {
		if(notValidate == null) {
			return false;
		}
		return notValidate;
	}

	public static IDFrameEnum findByName(String name) {
		for (IDFrameEnum en : IDFrameEnum.values()) {
			if (en.getName().equals(name)) {
				return en;
			}
		}
		throw new IllegalArgumentException("Invalid Name.");
	}

	public String toString() {
		return "{\"name\": " + name + "\"}";
	}
	
	
}