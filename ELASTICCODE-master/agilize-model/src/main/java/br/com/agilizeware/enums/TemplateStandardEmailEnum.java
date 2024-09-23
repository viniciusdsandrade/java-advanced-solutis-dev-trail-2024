package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TemplateStandardEmailEnumDeserializer.class)
public enum TemplateStandardEmailEnum implements IEnum<Integer> {
	
	STANDARD(0, "STANDARD", "/templates/email_default.vm"),
	CONTACT_AGILIZE(1, "CONTACT_AGILIZE", "/templates/email_contact_agilize.vm"),
	;
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private TemplateStandardEmailEnum(Integer codigo, String descricao, String label) {
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


	public static TemplateStandardEmailEnum findByLabel(String label) {
		for (TemplateStandardEmailEnum userType : TemplateStandardEmailEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static TemplateStandardEmailEnum findByCode(Integer code) {
		TemplateStandardEmailEnum[] array = TemplateStandardEmailEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
