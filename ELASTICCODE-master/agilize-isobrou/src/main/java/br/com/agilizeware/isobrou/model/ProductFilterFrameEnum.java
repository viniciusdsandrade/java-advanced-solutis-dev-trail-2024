package br.com.agilizeware.isobrou.model;

import br.com.agilizeware.enums.IFrameEnum;

public enum ProductFilterFrameEnum implements IFrameEnum {

	USER("idUser", 12, IFrameEnum.LTE, false, "lbl.user", true, null, null, null, null, null, null),
	NO_ADDRESS("flgWithoutAddress", null, null, true, "lbl.flg.without.address", null, null, null, null, null, null, null),
	ADDRESS("address", null, null, false, "lbl.address", false, null, null, null, AddressFilterFrameEnum.values(), null, null),
	NAME("name", 160, IFrameEnum.LTE, false, "lbl.product.name", false, null, null, null, null, null, null),
	TYPE("type", null, null, false, "lbl.product.type", null, null, null, null, TypeProductFrameEnum.values(), null, null),
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
	
	private ProductFilterFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
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
		if(required == null) {
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
	
	public Boolean isNotValidate() {
		if(notValidate == null) {
			return false;
		}
		return notValidate;
	}
	
	public IFrameEnum[] getNorOperations() {
		IFrameEnum[] frames = new IFrameEnum[3];
		frames[0] = ProductFilterFrameEnum.USER;
		frames[1] = ProductFilterFrameEnum.ADDRESS;
		frames[2] = ProductFilterFrameEnum.NO_ADDRESS;
		return frames;
	}

	public static ProductFilterFrameEnum findByName(String name) {
		for (ProductFilterFrameEnum en : ProductFilterFrameEnum.values()) {
			if (en.getName().equals(name)) {
				return en;
			}
		}
		throw new IllegalArgumentException("Invalid Name.");
	}
	
	public String toString() {
		return "{\"name\": "+name+"\"}";
	}

}
