package br.com.agilizeware.enums;

public interface IFrameEnum {

	String EQ = "EQ";
	String LTE = "LTE";
	String GTE = "GTE";
	String DTHOUR = "DTHOUR";
	String DTWITHOUTHOUR = "DTWITHOUTHOUR";
	String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	String PATTERN_CNPJ = "[0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}\\/?[0-9]{4}\\-?[0-9]{2}";
	String PATTERN_CPF = "[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}";
	String PATTERN_PHONE_8_DIGITS = "\\(\\d{2}\\) \\d{4,4}-\\d{4,4}";
	String PATTERN_PHONE_9_DIGITS = "\\(\\d{2}\\) \\d{5,5}-\\d{4,4}";
	
	Boolean isRequired();
	String getName();
	String getLabel();
	Integer getLength();
	Boolean isNumeric();
	String getOperLength();
	String getClassEnum();
	String getPatternDate();
	String regex();
	IFrameEnum[] getDaughters();
	IFrameEnum[] getNorOperations();
	Boolean isNotValidate();
	Boolean isList();
}
