package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TypeAuthenticationEnumDeserializer.class)
public enum TypeAuthenticationEnum implements IEnum<Integer> {
	
	EMAIL(0, "EMAIL", "lbl.email"),
	CPF(1, "CPF", "lbl.cpf"),
	;
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private TypeAuthenticationEnum(Integer codigo, String descricao, String label) {
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


	public static TypeAuthenticationEnum findByLabel(String label) {
		for (TypeAuthenticationEnum userType : TypeAuthenticationEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static TypeAuthenticationEnum findByCode(Integer code) {
		TypeAuthenticationEnum[] array = TypeAuthenticationEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
