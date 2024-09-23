package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RelationshipEnumDeserializer.class)
public enum RelationShipEnum implements IEnum<Integer> {
	
	ONE_TO_ONE(0, "ONE_TO_ONE", "lbl.one.to.one"),
	ONE_TO_MANY(1, "ONE_TO_MANY", "lbl.one.to.many"),
	MANY_TO_ONE(2, "MANY_TO_ONE", "lbl.many.to.one"),
	MANY_TO_MANY(3, "MANY_TO_MANY", "lbl.many.to.many");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private RelationShipEnum(Integer codigo, String descricao, String label) {
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


	public static RelationShipEnum findByLabel(String label) {
		for (RelationShipEnum userType : RelationShipEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static RelationShipEnum findByCode(Integer code) {
		RelationShipEnum[] array = RelationShipEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
