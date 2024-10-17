package com.mightyjava.utils;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ErrorUtils {
	public static String customErrors(List<ObjectError> errors) {
		JSONObject jsonObject = new JSONObject();
		try {
			StringBuilder errorMesssage = new StringBuilder();
			jsonObject.put("status", "failure");
			jsonObject.put("title", "Field Errors");
			for (ObjectError objectError : errors) {
				if(objectError instanceof FieldError) {
					FieldError fieldError = (FieldError) objectError;
					errorMesssage.append("<b>").append(MethodUtils.convertString(fieldError.getField())).append(" : </b>").append(fieldError.getDefaultMessage()).append("</br>");
				}
			}
			jsonObject.put("message", errorMesssage.toString());
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return jsonObject.toString();
	}
}
