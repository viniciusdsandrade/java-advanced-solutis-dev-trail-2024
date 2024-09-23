package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = WorkFlowEnumDeserializer.class)
public enum WorkFlowEnum implements IEnum<Integer> {
	
	ISOBROU(0, "StoreWF", "/src/main/resources/process/isobrou/StoreWF.bpmn"),
	;
	
	private Integer codigo;
	private String descricao;
	private String path;
	
	private WorkFlowEnum(Integer codigo, String descricao, String path) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.path = path;
	}
	
	public Integer getId() {
		return codigo;
	}

	public String getLabel() {
		return null;
	}

	public String getDescription() {
		return descricao;
	}

	public String getPath() {
		return this.path;
	}

	public static WorkFlowEnum findByLabel(String label) {
		for (WorkFlowEnum userType : WorkFlowEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static WorkFlowEnum findByCode(Integer code) {
		WorkFlowEnum[] array = WorkFlowEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"path\": \""+path+"\"}";
	}
	
}
