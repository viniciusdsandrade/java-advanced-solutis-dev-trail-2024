package br.com.agilizeware.payment.util;

import br.com.agilizeware.util.Util;

public class RegexCardValidator {
	
	public static boolean isValidCard(String number, String brand) {
		
		String card = Util.onlyNumbers(number); // remove all non-numerics
        if (!Util.isNotNull(card) || !Util.isNotNull(brand) || card.length() < 13 || card.length() > 19) {
            return false;
        }
        
        if (!luhnCheck(card)) {
            return false;
        }
        
        CardCompany cc = CardCompany.gleanCompany(card);
        if(!Util.isNotNull(cc)) {
        	return false;
        }
        
        if(!cc.getName().equalsIgnoreCase(brand) && !((cc.getId().equals(4) || cc.getId().equals(6)) && 
        		(brand.equalsIgnoreCase(CardCompany.DISCOVER.getName()) || brand.equalsIgnoreCase(CardCompany.ELO.getName()) ) )) {
        	return false;
        }
        
        return true;
	}
	
	/**
     * Checks for a valid credit card number.
     * @param cardNumber Credit Card Number.
     * @return Whether the card number passes the luhnCheck.
     */
    private static boolean luhnCheck(String cardNumber) {
        // number must be validated as 0..9 numeric first!!
        int digits = cardNumber.length();
        int oddOrEven = digits & 1;
        long sum = 0;
        for (int count = 0; count < digits; count++) {
            int digit = 0;
            try {
                digit = Integer.parseInt(cardNumber.charAt(count) + "");
            } catch(NumberFormatException e) {
                return false;
            }

            if (((count & 1) ^ oddOrEven) == 0) { // not
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }

        return (sum == 0) ? false : (sum % 10 == 0);
    }
}
