package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = BrandEnumDeserializer.class)
public enum BrandEnum implements IEnum<Integer> {
	
	MASTER(0, "Master", "lbl.card.master"),
	VISA(1, "Visa", "lbl.card.visa"),
	AMEX(2, "Amex", "lbl.card.amex"),
	ELO(3, "Elo", "lbl.card.elo"),
	DINNERS(4, "Dinners", "lbl.card.dinners");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private BrandEnum(Integer codigo, String descricao, String label) {
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

	public static BrandEnum findByLabel(String label) {
		for (BrandEnum userType : BrandEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static BrandEnum findByDescription(String description) {
		for (BrandEnum brand : BrandEnum.values()) {
			if (brand.getDescription().equals(description)) {
				return brand;
			}
		}
		throw new IllegalArgumentException("Invalid Description.");
	}
	
	public static BrandEnum findByCode(Integer code) {
		BrandEnum[] array = BrandEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
