package br.com.agilizeware.isobrou.model;

import br.com.agilizeware.enums.IFrameEnum;

public enum StoreFrameEnum implements IFrameEnum {

	COMPANY_NAME("companyName", 160, IFrameEnum.LTE, true, "lbl.social.reason", false, null, null, null, null, null, null), 
	FANTASY_NAME("fantasyName", 160, IFrameEnum.LTE, false, "lbl.name.fantasy", false, null, null, null, null, null, null), 
	CNPJ("cnpj", 14, IFrameEnum.LTE, true, "lbl.cnpj", true, null, null, IFrameEnum.PATTERN_CNPJ, null, null, null), 
	INSTANCE_FLOW("instanceFlow", 500, IFrameEnum.LTE, false, "lbl.instance.flow", false, null, null, null, null, null, null), 
	COMPLEMENTARY_INFORMATION("complementaryInformation", 400, IFrameEnum.LTE, false, "lbl.complementary.information", false, null, null, null, null, null, null), 
	PASSWORD("password", 8, IFrameEnum.GTE, true, "lbl.password", false, null, null, null, null, null, null), 
	COLLECTION("Estabelecimento", null, null, null, "lbl.store", null, null, null, null, null, true, null), 
	FILE("image", null, null, false, "lbl.photo", null, null, null, null, null, true, null),
	APPLICATION("idApplication", 12, IFrameEnum.LTE, false, "lbl.application", true, null, null, null, null, null, null),
	ADDRESS("address", null, null, true, "lbl.address", null, null, null, null, AddressFrameEnum.values(), null, null),
	SPONSOR("sponsor", 160, IFrameEnum.LTE, true, "lbl.sponsor", false, null, null, null, null, null, null), 
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
	
	private StoreFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
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