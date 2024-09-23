package br.com.agilizeware.isobrou.model;

import br.com.agilizeware.enums.IFrameEnum;

public enum AddressFrameEnum implements IFrameEnum {

	COMMERCIAL_PHONE("commercialPhone", 11, IFrameEnum.LTE, false, "lbl.commercial.phone", true, null, null, null, null, null, null), 
	RESIDENCIAL_PHONE("residencialPhone", 11, IFrameEnum.LTE, false, "lbl.residencial.phone", true, null, null, null, null, null, null), 
	CELL_PHONE("cellPhone", 11, IFrameEnum.LTE, true, "lbl.cell.phone", true, null, null, null, null, null, null), 
	EMAIL("email", 120, IFrameEnum.LTE, true, "lbl.email", false, null, null, IFrameEnum.PATTERN_EMAIL, null, null, null), 
	ZIP_CODE("zipCode", 8, IFrameEnum.LTE, true, "lbl.cep", false, null, null, null, null, null, null), 
	STREET("street", 100, IFrameEnum.LTE, true, "lbl.street", false, null, null, null, null, null, null), 
	NUMBER("number", 4, IFrameEnum.LTE, true, "lbl.number", true, null, null, null, null, null, null), 
	COMPLEMENT("complement", 120, IFrameEnum.LTE, false, "lbl.complement", false, null, null, null, null, null, null), 
	REFERENCE_POINT("referencePoint", 160, IFrameEnum.LTE, false, "lbl.reference.point", false, null, null, null, null, null, null), 
	NEIGHBORHOOD("neighborhood", 120, IFrameEnum.LTE, true, "lbl.neighborhood", false, null, null, null, null, null, null), 
	FAX("fax", 11, IFrameEnum.LTE, false, "lbl.fax", true, null, null, null, null, null, null),
	CITY("city", null, null, true, "lbl.city", null, null, null, null, CityFrameEnum.values(), null, null), 
	LOCATION("location", null, null, true, "lbl.location", null, null, null, null, LocationFrameEnum.values(), null, null), 
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
	
	private AddressFrameEnum(String name, Integer length, String operLength, Boolean required, String label,
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

	public static AddressFrameEnum findByName(String name) {
		for (AddressFrameEnum en : AddressFrameEnum.values()) {
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
