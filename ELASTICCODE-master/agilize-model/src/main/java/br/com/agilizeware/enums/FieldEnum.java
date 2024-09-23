package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FieldEnumDeserializer.class)
public enum FieldEnum implements IEnum<Integer> {
	
	RELATIONSHIP(0, "RELATIONSHIP", "lbl.relationship"),
	TEXT(1, "TEXT", "lbl.text"),
	DATE(2, "DATE", "lbl.date"),
	NUMERIC(3, "NUMERIC", "lbl.numeric"),
	VALUE(4, "MONETARY", "lbl.monetary"),
	EMAIL(5, "EMAIL", "lbl.email"),
	CPF(6, "CPF", "lbl.cpf"),
	CNPJ(7, "CNPJ", "lbl.cnpj");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private FieldEnum(Integer codigo, String descricao, String label) {
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

	public static FieldEnum findByLabel(String label) {
		for (FieldEnum userType : FieldEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static FieldEnum findByCode(Integer code) {
		FieldEnum[] array = FieldEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
