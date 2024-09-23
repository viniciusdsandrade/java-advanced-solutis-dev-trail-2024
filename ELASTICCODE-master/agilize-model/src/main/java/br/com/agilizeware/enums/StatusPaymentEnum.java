package br.com.agilizeware.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = StatusPaymentEnumDeserializer.class)
public enum StatusPaymentEnum implements IEnum<Integer> {
	
	NOT_FINISHED(0, "NOT_FINISHED", "lbl.payment.not.finished"),
	AUTHORIZED(1, "AUTHORIZED", "lbl.payment.authorized"),
	CONFIRMED_PAYMENT(2, "CONFIRMED_PAYMENT", "lbl.payment.confirmed.paymed"),
	DENIED(3, "DENIED", "lbl.payment.denied"),
	VOIDED(10, "VOIDED", "lbl.payment.voided"),
	REFUNDED(11, "REFUNDED", "lbl.payment.refunded"),
	PENDING(12, "PENDING", "lbl.payment.pending"),
	ABORTED(13, "ABORTED", "lbl.payment.aborted"),
	SCHEDULED(20, "SCHEDULED", "lbl.payment.scheduled");
	
	private Integer codigo;
	private String descricao;
	private String label;
	
	private StatusPaymentEnum(Integer codigo, String descricao, String label) {
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

	public static StatusPaymentEnum findByLabel(String label) {
		for (StatusPaymentEnum userType : StatusPaymentEnum.values()) {
			if (userType.getLabel().equals(label)) {
				return userType;
			}
		}
		throw new IllegalArgumentException("Invalid label.");
	}
	
	public static StatusPaymentEnum findByCode(Integer code) {
		StatusPaymentEnum[] array = StatusPaymentEnum.values();
		return array[code];
	}
	
	public String toString() {
		return "{\"codigo\": "+codigo+", \"descricao\": \""+descricao+"\", \"label\": \""+label+"\"}";
	}
	
}
