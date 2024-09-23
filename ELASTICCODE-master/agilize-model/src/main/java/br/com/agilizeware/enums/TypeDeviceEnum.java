package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TypeDeviceEnumDeserializer.class)
public enum TypeDeviceEnum implements IEnum<Integer> {
	
	UNDEFINED(0, "UNDEFINED", "lbl.device.undefined"),
	MOBILE(1, "MOBILE", "lbl.device.mobile"),
	TABLET(2, "TABLET", "lbl.device.tablet"),
	DESKTOP(3, "DESKTOP", "lbl.device.desktop");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private TypeDeviceEnum(Integer codigo, String descricao, String label) {
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


	public static TypeDeviceEnum findByLabel(String label) {
		for (TypeDeviceEnum userType : TypeDeviceEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static TypeDeviceEnum findByCode(Integer code) {
		TypeDeviceEnum[] array = TypeDeviceEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
