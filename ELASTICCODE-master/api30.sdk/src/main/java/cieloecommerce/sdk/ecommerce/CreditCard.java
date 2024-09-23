package cieloecommerce.sdk.ecommerce;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CreditCard implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8286479920834249325L;

	@SerializedName("CardNumber")
	private String cardNumber;

	@SerializedName("Holder")
	private String holder;

	@SerializedName("ExpirationDate")
	private String expirationDate;

	@SerializedName("SecurityCode")
	private String securityCode;

	@SerializedName("SaveCard")
	private boolean saveCard = false;

	@SerializedName("Brand")
	private String brand;

	@SerializedName("CardToken")
	private String cardToken;
	
	public CreditCard() {}

	public CreditCard(String securityCode, String brand) {
		setSecurityCode(securityCode);
		setBrand(brand);
	}

	public String getBrand() {
		return brand;
	}

	public CreditCard setBrand(String brand) {
		this.brand = brand;
		return this;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public CreditCard setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	public String getCardToken() {
		return cardToken;
	}

	public CreditCard setCardToken(String cardToken) {
		this.cardToken = cardToken;
		return this;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public CreditCard setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
		return this;
	}

	public String getHolder() {
		return holder;
	}

	public CreditCard setHolder(String holder) {
		this.holder = holder;
		return this;
	}

	public boolean isSaveCard() {
		return saveCard;
	}

	public CreditCard setSaveCard(boolean saveCard) {
		this.saveCard = saveCard;
		return this;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public CreditCard setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
		return this;
	}
}
