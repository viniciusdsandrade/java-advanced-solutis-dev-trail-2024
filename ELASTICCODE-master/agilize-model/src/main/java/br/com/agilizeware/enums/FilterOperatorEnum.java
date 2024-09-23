package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FilterOperatorEnumDeserializer.class)
public enum FilterOperatorEnum implements IEnum<Integer> {
	
	 EQ (0, "EQ", "lbl.filter.op.equal"),
	 GE (1, "GE", "lbl.filter.op.greater.than.or.equal"),
	 LE (2, "LE", "lbl.filter.op.less.than.or.equal"),
	 GT (3, "GT", "lbl.filter.op.greater.than"),
	 LT (4, "LT", "lbl.filter.op.less.than"),
	 IN (5, "IN", "lbl.filter.op.in"),
	 LIKE (6, "LIKE", "lbl.filter.op.like"),
	 CONTAINS (7, "CONTAINS", "lbl.filter.op.contains"),
	 STARTSWITH (8, "STARTSWITH", "lbl.filter.op.starts.with"),
	 ENDSWITH (9, "ENDSWITH", "lbl.filter.op.ends.with"),
	 ISEMPTY (10, "ISEMPTY", "lbl.filter.op.is.empty"),
	 ISNOTEMPTY (11, "ISNOTEMPTY", "lbl.filter.op.is.not.empty"),
	 BETWEEN(12, "BETWEEN", "lbl.filter.op.between"),
	 PHONETIC(13, "PHONETIC", "lbl.filter.phonetic.search"),
	;	
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private FilterOperatorEnum(Integer codigo, String descricao, String label) {
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


	public static FilterOperatorEnum findByLabel(String label) {
		for (FilterOperatorEnum userType : FilterOperatorEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static FilterOperatorEnum findByCode(Integer code) {
		FilterOperatorEnum[] array = FilterOperatorEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}

}