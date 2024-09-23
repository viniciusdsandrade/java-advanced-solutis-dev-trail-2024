package br.com.agilizeware.shopping.model;

import br.com.agilizeware.enums.IFrameEnum;

public enum OrderFrameEnum implements IFrameEnum {

	COLLECTION("Pedido", null, null, null, "lbl.order", null, null, null, null, null, true, null),
	APPLICATION("idApplication", 12, IFrameEnum.LTE, false, "lbl.application", true, null, null, null, null, null, null),
	CUSTOMER("idUser", 12, IFrameEnum.LTE, false, "lbl.id.user", true, null, null, null, null, null, null),
	ORDERS("orders", null, null, true, "lbl.order", null, null, null, null, ProductFrameEnum.values(), null, true),
	TOTAL_VALUE("totalValue", 12, IFrameEnum.LTE, false, "lbl.value", true, null, null, null, null, null, null),	
	DT_ORDER("dtOrder", 10, IFrameEnum.LTE, false, "lbl.dt.order", false, null, IFrameEnum.DTHOUR, null, null, null, null),
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

	private OrderFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
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

	public Boolean isList() {
		if(isList == null) {
			return false;
		}
		return isList;
	}
	
	public static OrderFrameEnum findByName(String name) {
		for (OrderFrameEnum en : OrderFrameEnum.values()) {
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
