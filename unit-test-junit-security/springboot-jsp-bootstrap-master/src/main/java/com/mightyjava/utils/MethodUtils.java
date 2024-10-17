package com.mightyjava.utils;

public class MethodUtils {
	public static String convertString(String text) {
		StringBuilder formattedText = new StringBuilder();
		for(Character character : text.toCharArray()) {
			if(Character.isUpperCase(character)) formattedText.append(" ").append(character);
			else formattedText.append(character);
			formattedText = new StringBuilder(formattedText.substring(0, 1).toUpperCase() + formattedText.substring(1));
		}
		return formattedText.toString().trim();
	}
}
