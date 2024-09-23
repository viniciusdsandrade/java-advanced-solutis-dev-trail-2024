package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = NotYetEnumDeserializer.class)
public enum NotYetEnum implements IEnum<Integer> {
	
	NOT(0, "Not", "lbl.not"),
	YES(1, "Yes", "lbl.yes"),
	;
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private NotYetEnum(Integer codigo, String descricao, String label) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.label = label;
	}
	
	public Integer getId() {
		return codigo;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return descricao;
	}

	public static NotYetEnum findByLabel(String label) {
		for (NotYetEnum userType : NotYetEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static NotYetEnum findByDescription(String description) {
		for (NotYetEnum brand : NotYetEnum.values()) {
			if (brand.getDescription().equals(description)) {
				return brand;
			}
		}
		throw new IllegalArgumentException("Invalid Description.");
	}
	
	public static NotYetEnum findByCode(Integer code) {
		NotYetEnum[] array = NotYetEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
