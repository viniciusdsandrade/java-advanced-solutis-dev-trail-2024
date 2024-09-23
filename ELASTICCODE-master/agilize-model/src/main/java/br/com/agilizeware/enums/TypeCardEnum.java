package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TypeCardEnumDeserializer.class)
public enum TypeCardEnum implements IEnum<Integer> {
	
	CREDIT(0, "CREDIT", "lbl.credit"),
	DEBIT(1, "DEBIT", "lbl.debit");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private TypeCardEnum(Integer codigo, String descricao, String label) {
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

	public static TypeCardEnum findByLabel(String label) {
		for (TypeCardEnum userType : TypeCardEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static TypeCardEnum findByCode(Integer code) {
		TypeCardEnum[] array = TypeCardEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
