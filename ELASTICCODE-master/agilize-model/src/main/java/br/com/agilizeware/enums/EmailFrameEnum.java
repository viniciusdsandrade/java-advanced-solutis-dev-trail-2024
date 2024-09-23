package br.com.agilizeware.enums;

public enum EmailFrameEnum {

	ADDRESS("to", 160),
	SUBJECT("subject", 120),
	TEMPLATE_STANDARD("codTemplateStandard", 2),
	TEMPLATE_EXTERNAL("idFileTemplate", 12),
	ATTACHMENT("idFileAttachment", 12),
	PARAMETERS("parameters", null),
	;
	
	private String name;
	private Integer length;
	
	private EmailFrameEnum(String name, Integer length) {
		this.name = name;
		this.length = length;
	}
	
	public String getName() {
		return this.name;
	}

	public Integer getLength() {
		return length;
	}

	public static EmailFrameEnum findByName(String name) {
		for (EmailFrameEnum en : EmailFrameEnum.values()) {
			if (en.getName().equals(name)) {
				return en;
			}
		}
		throw new IllegalArgumentException("Invalid Name.");
	}
	
	public String toString() {
		return "{\"name\": "+name+"\"}";
	}

}
