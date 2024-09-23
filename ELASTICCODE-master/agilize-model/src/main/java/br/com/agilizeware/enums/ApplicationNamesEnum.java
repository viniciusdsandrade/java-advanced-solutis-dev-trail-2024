package br.com.agilizeware.enums;

public enum ApplicationNamesEnum {
	
	ELASTICCODE(0, "ELASTICCODE"),
	FILESERVER(1, "FILESERVER"),
	WORKFLOW(2, "WORKFLOW"),
	ISOBROU(3, "ISOBROU"),
	PAYMENT(4, "PAYMENT"),
	ASSYNCRONO(5, "ASYNCROUNOUS"),
	SHOPPING(6, "SHOPPING"),
	;
	
	private Integer codigo;
	private String name;
	
	private ApplicationNamesEnum(Integer codigo, String name) {
		this.codigo = codigo;
		this.name = name;
	}
	
	public Integer getId() {
		return codigo;
	}

	public String getName() {
		return this.name;
	}

	public static ApplicationNamesEnum findByCode(Integer code) {
		ApplicationNamesEnum[] array = ApplicationNamesEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"name\": \""+name+"\"}";
	}
	
}
