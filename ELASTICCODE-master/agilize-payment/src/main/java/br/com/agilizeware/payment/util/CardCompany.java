package br.com.agilizeware.payment.util;

public enum CardCompany {

	VISA (0, "^4[0-9]{12}(?:[0-9]{3})?$", "Visa"),
    MASTERCARD (1, "^5[1-5][0-9]{14}$", "Master"),
    AMEX (2, "^3[47][0-9]{13}$", "Amex"),
    DINERS (3, "^3(?:0[0-5]|[68][0-9])[0-9]{11}$", "Dinners"),
    ELO (4, "^6(?:011|5[0-9]{2})[0-9]{12}$", "Elo"),
    JCB (5, "^(?:2131|1800|35\\d{3})\\d{11}$", "JCB"),
    DISCOVER (6, "^6(?:011|5[0-9]{2})[0-9]{12}$", "Discover"),
    ;
	
	private Integer id;
	private String regex;
    private String name;
    
    CardCompany(Integer id, String regex, String name) {
    	this.id = id;
        this.regex = regex;
        this.name = name;
    }
    
    public boolean matches(String card) {
        return card.matches(this.regex);
    }
    
    /**
     * get an enum from a card number
     * @param card
     * @return
     */
    public static CardCompany gleanCompany(String card) {
        for (CardCompany cc : CardCompany.values()){
            if (cc.matches(card)) {
                return cc;
            }
        }
        return null;
    }

    /**
     * get an enum from an issuerName
     * @param issuerName
     * @return
     */
    public static CardCompany gleanCompanyByIssuerName(String name) {
        for (CardCompany cc : CardCompany.values()){
            if (cc.name().equals(name)) {
                return cc;
            }
        }
        return null;
    }

	public Integer getId() {
		return id;
	}

	public String getRegex() {
		return regex;
	}

	public String getName() {
		return name;
	}
    
}
