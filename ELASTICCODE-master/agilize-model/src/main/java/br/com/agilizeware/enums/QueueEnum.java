package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = QueueEnumDeserializer.class)
public enum QueueEnum implements IEnum<Integer> {
	
	MAIL(0, "MAIL", "lbl.async.email"),
	EXTERNAL(1, "EXTERNAL_SERVICES", "lbl.async.external.services");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private QueueEnum(Integer codigo, String descricao, String label) {
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

	public static QueueEnum findByLabel(String label) {
		for (QueueEnum userType : QueueEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static QueueEnum findByCode(Integer code) {
		QueueEnum[] array = QueueEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
