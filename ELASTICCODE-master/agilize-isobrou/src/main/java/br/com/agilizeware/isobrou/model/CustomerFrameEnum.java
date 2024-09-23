package br.com.agilizeware.isobrou.model;

import br.com.agilizeware.enums.IFrameEnum;

public enum CustomerFrameEnum implements IFrameEnum {

	NAME("name", 160, IFrameEnum.LTE, true, "lbl.name", false, null, null, null, null, null, null),
	RG("rg", 20, IFrameEnum.LTE, false, "lbl.rg", false, null, null, null, null, null, null),
	CPF("cpf", 11, IFrameEnum.LTE, false, "lbl.cpf", false, null, null, IFrameEnum.PATTERN_CPF, null, null, null),
	BIRTHDAY("birthday", 10, IFrameEnum.LTE, false, "lbl.birthday", false, null, IFrameEnum.DTWITHOUTHOUR, null, null, null, null),
	PHOTO("image", null, null, false, "lbl.photo", null, null, null, null, null, true, null),
	PASSWORD("password", 8, IFrameEnum.GTE, true, "lbl.password", false, null, null, null, null, null, null),
	DEVICE("device", 1, IFrameEnum.LTE, true, "lbl.photo", true, null, null, null, null, null, null),
	USER("idUser", 12, IFrameEnum.LTE, false, "lbl.user", true, null, null, null, null, null, null),
	ADDRESS("address", null, null, true, "lbl.address", null, null, null, null, AddressFrameEnum.values(), null, null),
	APPLICATION("idApplication", 12, IFrameEnum.LTE, false, "lbl.application", true, null, null, null, null, null, null),
	COLLECTION("customer", null, null, null, "lbl.customer", null, null, null, null, null, true, null),
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
	
	private CustomerFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
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
		IFrameEnum[] frames = new IFrameEnum[2];
		frames[0] = ProductFilterFrameEnum.USER;
		frames[1] = ProductFilterFrameEnum.ADDRESS;
		return frames;
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
