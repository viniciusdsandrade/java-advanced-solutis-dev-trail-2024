package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PageableTypePredicateEnumDeserializer.class)
public enum PageableTypePredicateEnum implements IEnum<Integer> {
	
	OR(0,"or", "lbl.or"), 
	FILTER(1, "filter", "lbl.filter"), 
	AND(2, "and", "lbl.and"),
	;
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private PageableTypePredicateEnum(Integer codigo, String descricao, String label) {
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


	public static PageableTypePredicateEnum findByLabel(String label) {
		for (PageableTypePredicateEnum userType : PageableTypePredicateEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static PageableTypePredicateEnum findByCode(Integer code) {
		PageableTypePredicateEnum[] array = PageableTypePredicateEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
}