package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TypePersonEnumDeserializer.class)
public enum TypePersonEnum implements IEnum<Integer> {
	
	PHYSICAL(0, "PHYSICAL", "lbl.type.person.physical"),
	LEGAL(1, "LEGAL", "lbl.type.person.legal");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private TypePersonEnum(Integer codigo, String descricao, String label) {
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

	public static TypePersonEnum findByLabel(String label) {
		for (TypePersonEnum userType : TypePersonEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static TypePersonEnum findByCode(Integer code) {
		TypePersonEnum[] array = TypePersonEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
