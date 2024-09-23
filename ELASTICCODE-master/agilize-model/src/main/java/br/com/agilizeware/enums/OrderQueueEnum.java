package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OrderQueueEnumDeserializer.class)
public enum OrderQueueEnum implements IEnum<Integer> {
	
	HIGHEST(0, "HIGHEST", "lbl.async.order.highest"),
	MEDIUM(1, "MEDIUM", "lbl.async.order.medium"),
	MINIMAL(2, "MINIMAL", "lbl.async.order.minimal");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private OrderQueueEnum(Integer codigo, String descricao, String label) {
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


	public static OrderQueueEnum findByLabel(String label) {
		for (OrderQueueEnum userType : OrderQueueEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static OrderQueueEnum findByCode(Integer code) {
		OrderQueueEnum[] array = OrderQueueEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
