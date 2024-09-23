package br.com.agilizeware.isobrou.model;

import br.com.agilizeware.enums.IFrameEnum;

public enum ProductFrameEnum implements IFrameEnum {

	NAME("name", 160, IFrameEnum.LTE, true, "lbl.name", false, null, null, null, null, null, null),
	DESCRIPTION("description", 400, IFrameEnum.LTE, true, "lbl.description", false, null, null, null, null, null, null),
	TYPE("type", null, null, true, "lbl.product.type", null, null, null, null, TypeProductFrameEnum.values(), null, null),
	DT_EXPIRATION("dtExpiration", 10, IFrameEnum.LTE, true, "lbl.dt.expiration", false, null, IFrameEnum.DTWITHOUTHOUR, null, null, null, null),
	VALUE("value", 12, IFrameEnum.LTE, true, "lbl.value", true, null, null, null, null, null, null),
	AMOUNT("amount", 4, IFrameEnum.LTE, true, "lbl.amount", true, null, null, null, null, null, null),
	STORE("store", null, null, true, "lbl.store", null, null, null, null, StoreFrameEnum.values(), null, null),
	APPLICATION("idApplication", 12, IFrameEnum.LTE, false, "lbl.application", true, null, null, null, null, null, null),
	FILE("image", null, null, false, "lbl.photo", null, null, null, null, null, true, null),
	COLLECTION("Produto", null, null, null, "lbl.product", null, null, null, null, null, true, null),
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
	
	private ProductFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
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

	public static ProductFilterFrameEnum findByName(String name) {
		for (ProductFilterFrameEnum en : ProductFilterFrameEnum.values()) {
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
