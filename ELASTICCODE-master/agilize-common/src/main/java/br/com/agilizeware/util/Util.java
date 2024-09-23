package br.com.agilizeware.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MaskFormatter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.agilizeware.dto.RestErrorDto;
import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.TypeDeviceEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;

public class Util {
	
	private static final PasswordEncoder passwEncoder = new SaltedSHA256PasswordEncoder();
	
	public static Locale locale = LocaleContextHolder.getLocale();
	private static final Logger log = LogManager.getLogger(Util.class);
    public static final SimpleDateFormat sdfWithoutHour = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat sdfBrazilWithHMS = new SimpleDateFormat("dd/MM/yyyy hh:MM:ss");

	public static final String HEADER_SERVICE_KEY = "X-Auth-Service";
	public static final String HEADER_PATH_KEY = "X-Header-Path";
	public static final String HEADER_APPLICATION_KEY = "X-Header-Application";
	private static Map<String, Application> mapApps = new HashMap<String, Application>();
    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static int calcularDigito(String str, int[] peso) {
    	int soma = 0;
    	for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
    		digito = Integer.parseInt(str.substring(indice,indice+1));
    		soma += digito*peso[peso.length-str.length()+indice];
    	}
    	soma = 11 - soma % 11;
    	return soma > 9 ? 0 : soma;
    }

    public static boolean isValidCPF(String cpf) {
    	if ((cpf==null) || (cpf.length()!=11)) return false;

      	Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
      	Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
      	return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }

    public static boolean isValidCNPJ(String cnpj) {
    	if ((cnpj==null)||(cnpj.length()!=14)) return false;

    	Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
    	Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
    	return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }

    
    public static Date obterDataHoraAtual() {
        return new Date();
    }

    public static String getStringDateWithHour(Date dt) {
        return Util.sdfBrazilWithHMS.format(dt);
    }
    
    public static String getStringDateWithoutHour(Date dt) {
        return Util.sdfWithoutHour.format(dt);
    }

    public static boolean isNotNull(Object obj) {
        return obj != null && !obj.toString().isEmpty();
    }
    
    @SuppressWarnings("rawtypes") 
    public static boolean isListNotNull(List obj) {
        return obj != null && !obj.isEmpty();
    }
    
    @SuppressWarnings("rawtypes") 
    public static boolean isMapNotNull(Map obj) {
        return obj != null && !obj.isEmpty();
    }

    public static boolean compareDatesWhithoutHour(Date dtAfter, Date dtBefore) {
        try {
            Calendar cBefore = Calendar.getInstance();
            cBefore.setTime(Util.sdfWithoutHour.parse(Util.sdfWithoutHour.format(dtBefore)));
            Calendar cAfter = Calendar.getInstance();
            cAfter.setTime(Util.sdfWithoutHour.parse(Util.sdfWithoutHour.format(dtAfter)));
            return cAfter.after(cBefore);
        } catch (ParseException pae) {
            throw new RuntimeException(pae);
        }
    }
    
    @SuppressWarnings("rawtypes")
	public static Object accessRestService(String url, int operation, Object request, Class ret, 
    		String token, Map<String, ?> requestParams) {
    	return accessRestHeaderService(url, operation, request, ret, token, requestParams, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object accessRestHeaderService(String url, int operation, Object request, Class ret, 
    		String token, Map<String, ?> requestParams, Map<String, String> headerParams) {
    	
    	log.info("**** INICIO Util | accessRestService ****");
    	log.info("Url = "+url);
    	
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        UriComponentsBuilder uri = null;
        if (requestParams != null && !requestParams.isEmpty()) {
            uri = UriComponentsBuilder.fromHttpUrl(url);
            for (String str : requestParams.keySet()) {
                uri.queryParam(str, requestParams.get(str));
            }
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (token != null && !token.isEmpty() && !token.equals("-1")) {
            //headers.add("X-Auth-Token", token);
            headers.add(HEADER_SERVICE_KEY, token);
        }
        if(Util.isMapNotNull(headerParams)) {
        	for(String key : headerParams.keySet()) {
        		headers.add(key, headerParams.get(key));
        	}
        }
        
        HttpEntity entity = request != null ? new HttpEntity(request, headers) : new HttpEntity(headers);
        if (operation == 1) {
            if (uri != null) {
                response = restTemplate.getForEntity(uri.build().encode().toUri(), ret);
            } else {
                response = restTemplate.getForEntity(url, ret);
            }
        } else if (operation == 2) {
            if (uri != null) {
                response = restTemplate.postForEntity(uri.build().encode().toUri(), entity, ret);
            } else {
                response = restTemplate.postForEntity(url, entity, ret);
            }
        } else if (operation == 3) {
            if (uri != null) {
            	//restTemplate.put(uri.build().encode().toUri(), entity);
            	response = restTemplate.exchange(uri.build().encode().toUri(), HttpMethod.PUT, entity, ret);
            } else {
                //restTemplate.put(url, entity);
            	response = restTemplate.exchange(url, HttpMethod.PUT, entity, ret);
            }
        }
        else if (operation == 4) {
            /*if (uri != null) {
                restTemplate.delete(uri.build().encode().toUri());
            } else {
                restTemplate.delete(url, entity);
            }*/
        	if (uri != null) {
            	response = restTemplate.exchange(uri.build().encode().toUri(), HttpMethod.DELETE, entity, ret);
        	}
        	else {
            	response = restTemplate.exchange(url, HttpMethod.DELETE, entity, ret);
        	}
        }
        else if (operation == 5) {
        	if (uri != null) {
            	response = restTemplate.exchange(uri.build().encode().toUri(), HttpMethod.GET, entity, ret);
        	}
        	else {
            	response = restTemplate.exchange(url, HttpMethod.GET, entity, ret);
        	}
        }
        
        Object retObj = null;
        if (response != null && HttpStatus.OK == response.getStatusCode()) {
        	if(ret.equals(RestResultDto.class)) {
                return retObj = getResult(response.getBody());
        	}
            return retObj = response.getBody();
        }
        
    	log.info("Objeto a Retornar = "+ (isNotNull(retObj) ? retObj : "null"));
    	log.info("---- FIM Util | accessRestService ----");
        
    	return retObj;

    }

    public static Date getDateWhitHMS(String dt) {
    	
    	if(!isNotNull(dt)) {
    		return null;
    	}
    	
        try {
            return Util.sdfBrazilWithHMS.parse(dt);
        } 
        catch (ParseException pae) {
        	try {
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	        	return sdf.parse(dt.replaceAll("Z$", "+0000"));
        	}
		    catch(ParseException pae2) {
		    	throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_PARSE_DATE_EXCEPTION, pae2, 
		    			dt);
		    }
        }
    }
    
    public static Date getDateWhitoutHMS(String dt) {
        try {
            return Util.sdfWithoutHour.parse(dt);
        } catch (ParseException pae) {
        	try {
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        	return sdf.parse(dt);
        	}
		    catch(ParseException pae2) {
		    	throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_PARSE_DATE_EXCEPTION, pae2, 
		    			dt);
		    }
        }
    }

    public static Integer getDateDifferenceDays(Date dataLimitActiveParam) {
        long difference = dataLimitActiveParam.getTime() - Util.obterDataHoraAtual().getTime();
        // Quantidade de milissegundos em um dia
        int timeDay = 1000 * 60 * 60 * 24;
        Integer daysDifference = (int) (difference / timeDay);
        return daysDifference;
    }

    /**
     * Retorna diferen�a entre as data e a data atual.
     * Formato: { Sinal, dias, horas, minutos }
     * Sinal � positivo se dataUm for maior que dataDois.
     * @param dtOne
     * @param dtTwo
     * @return
     */
    public static int[] getDateSignalDifferenceDaysHoursMinutes(Date dtOne, Date dtTwo) {
        long difference = dtOne.getTime() - dtTwo.getTime();
        int[] diff = new int[4];
        diff[0] = (difference < 0 ? -1 : 1);
        difference = difference * diff[0];
        long allMinutes = Math.floorDiv(difference, (1000 * 60));
        long allHours = Math.floorDiv(allMinutes, 60);
        long allDays = Math.floorDiv(allHours, 24);
        diff[1] = (int) allDays;
        diff[2] = (int) (allHours - (allDays * 24));
        diff[3] = (int) (allMinutes - (allHours * 60));
        return diff;
    }

    public static int[] getDateSignalDifferenceDaysHoursMinutes(Date dataLimitActiveParam) {
        return Util.getDateSignalDifferenceDaysHoursMinutes(dataLimitActiveParam, Util.obterDataHoraAtual());
    }
    
    public static int[] getDateSignalDifferenceDaysHoursMinutes(String dataLimitActiveParam) {
    	
    	if(dataLimitActiveParam != null && !dataLimitActiveParam.isEmpty()) {
        	String[] strDate = dataLimitActiveParam.split("-");
        	Integer ano = Integer.valueOf(strDate[0]);    		
        	Integer mes = Integer.valueOf(strDate[1]) - 1;    		
        	Integer dia = Integer.valueOf(strDate[2].substring(0,2).trim());    		
        	String[] hhmm = strDate[2].substring(2).trim().split(":");   
        	Integer hora = Integer.valueOf(hhmm[0]);
        	Integer minuto = Integer.valueOf(hhmm[1]);
        	
        	GregorianCalendar gc = new GregorianCalendar(ano, mes, dia, hora, minuto);
        	return getDateSignalDifferenceDaysHoursMinutes(gc.getTime());
    	}
    	return null;
    }

	public static String onlyNumbers(String value) {
		if(value == null || value.isEmpty()) {
			return "";
		}
		return value.replaceAll("[^0-9]", "");
	}
	
	public static String removeSpecialCharacter(String text) { 
	     return text.replaceAll("[�����]", "a")   
	                 .replaceAll("[����]", "e")   
	                 .replaceAll("[����]", "i")   
	                 .replaceAll("[�����]", "o")   
	                 .replaceAll("[����]", "u")   
	                 .replaceAll("[�����]", "A")   
	                 .replaceAll("[����]", "E")   
	                 .replaceAll("[����]", "I")   
	                 .replaceAll("[�����]", "O")   
	                 .replaceAll("[����]", "U")   
	                 .replace('�', 'c')   
	                 .replace('�', 'C')   
	                 .replace('�', 'n')   
	                 .replace('�', 'N')
	                 .replaceAll("!", "")
	                 .replaceAll(" ", "")          
	                 .replaceAll ("\\[\\�\\`\\?!\\@\\#\\$\\%\\�\\*","")
	                 .replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]","")
	                 .replaceAll("[\\.\\;\\-\\_\\+\\'\\�\\�\\:\\;\\/]","");
	}
	
	public static String formatarNumericFields(String texto, String mascara) throws ParseException {
		texto = onlyNumbers(texto);
		if(texto.isEmpty()) {
			return "";
		}
        MaskFormatter mf = new MaskFormatter(mascara);
        mf.setValueContainsLiteralCharacters(false);
        return mf.valueToString(texto);
    }

	public static Integer getHttpMethod(String method) {
		if(isNotNull(method)) {
			if(HttpMethod.GET.name().equals(method)) {
				return HttpMethod.GET.ordinal();
			}
			else if(HttpMethod.POST.name().equals(method)) {
				return HttpMethod.POST.ordinal();
			}
			else if(HttpMethod.PUT.name().equals(method)) {
				return HttpMethod.PUT.ordinal();
			}
			else if(HttpMethod.DELETE.name().equals(method)) {
				return HttpMethod.DELETE.ordinal();
			}
			else if(HttpMethod.OPTIONS.name().equals(method)) {
				return HttpMethod.OPTIONS.ordinal();
			}
		}
		return null;
	}
    
	public static Field getField(Object object, String name) {

		if (!isNotNull(name) || object == null) {
			return null;
		}

		return getField2(object.getClass(), name.split("[.]"), 0);

	}
	
	private static Field getField2(Class<?> clazz, String[] names, int i) {

		if (!isNotNull(names) || clazz == null) {
			return null;
		}

		Field field = null;

		try {
			field = clazz.getDeclaredField(names[i]);
		} catch (NoSuchFieldException nse) {
			try {
				field = clazz.getSuperclass().getDeclaredField(names[i]);
			} catch (NoSuchFieldException nse2) {
				throw new RuntimeException(nse2);
			} catch (SecurityException e3) {
				throw new RuntimeException(e3);
			}
		} catch (SecurityException e2) {
			try {
				field = clazz.getSuperclass().getDeclaredField(names[i]);
			} catch (NoSuchFieldException nse2) {
				throw new RuntimeException(nse2);
			} catch (SecurityException e3) {
				throw new RuntimeException(e3);
			}
		}

		if (names.length == ++i) {
			return field;
		} else {
			return getField2(field.getType(), names, i);
		}

	}
	
	public static Date parseToDate(String dtToParse) {
	    SimpleDateFormat spdf = new SimpleDateFormat("dd/MM/yyyy");
	    Calendar cale = Calendar.getInstance(locale);
	    try {
	    	cale.setTime(spdf.parse(dtToParse));
	    }
	    catch(ParseException pae) {
		    try {
		    	spdf = new SimpleDateFormat("yyyy-MM-dd");
		    	cale.setTime(spdf.parse(dtToParse));
		    }
		    catch(ParseException pae2) {
		    	throw new AgilizeException(HttpStatus.PRECONDITION_FAILED.ordinal(), ErrorCodeEnum.ERROR_PARSE_DATE_EXCEPTION, pae2, dtToParse);
		    }
	    }
	    cale = toOnlyDate(cale);
	    return cale.getTime();
	  }
	
	public static Calendar toOnlyDate(Calendar date) {
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		
		return date;
	}
    
	public static String onlyAlphaNumeric(String value) {
		if(value == null || value.isEmpty()) {
			return "";
		}
		//return value.replaceAll("[^A-Za-z0-9]", "");
		return value.replaceAll("\\W", "");
	}
	
    /*@SuppressWarnings({"rawtypes", "unchecked"})
    public static HashMap<Object, Object> accessRestServiceHashMap(String url, int operation, Object request, Class ret, String token, Map<String, ?> requestParams) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        UriComponentsBuilder uri = null;
        if (requestParams != null && !requestParams.isEmpty()) {
            uri = UriComponentsBuilder.fromHttpUrl(url);
            for (String str : requestParams.keySet()) {
                uri.queryParam(str, requestParams.get(str));
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (token != null && !token.isEmpty() && !token.equals("-1")) {
            headers.add("X-Auth-Token", token);
        }
        HttpEntity entity = request != null ? new HttpEntity(request, headers) : new HttpEntity(headers);
        if (operation == 1) {
            if (uri != null) {
                response = restTemplate.getForEntity(uri.build().encode().toUri(), ret);
            } else {
                response = restTemplate.getForEntity(url, ret);
            }
        } else if (operation == 2) {
            if (uri != null) {
                response = restTemplate.postForEntity(uri.build().encode().toUri(), entity, ret);
            } else {
                response = restTemplate.postForEntity(url, entity, ret);
            }
        } else if (operation == 3) {
            if (uri != null) {
                restTemplate.put(uri.build().encode().toUri(), entity);
            } else {
                restTemplate.put(url, entity);
            }
        }
        if (response != null && HttpStatus.OK == response.getStatusCode()) {
            return (HashMap<Object, Object>) response.getBody();
        }
        return null;
    }*/

	/*public static void main(String args[]) {
		SaltedSHA256PasswordEncoder s = new SaltedSHA256PasswordEncoder();
		System.out.println(s.encode());
	}*/


	public static String generateUuid() {
		String ret = obterDataHoraAtual().getTime() + "_" + UUID.randomUUID().toString();
		if(ret.length() > 50) {
			return ret.substring(0, 50);
		}
		return ret;
	}
	
	public static boolean isAfterToday(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.parse(date).after(sdf.parse(sdf.format(obterDataHoraAtual())));
	}
	
	public static TypeDeviceEnum getTypeDeviceAtUserAgent(String userAgent) {
		
		if(isNotNull(userAgent)) {
			if(userAgent.indexOf("Mobile") != -1 || userAgent.matches("/iphone|ipod|android|blackberry|opera|mini|windows\\sce|palm|smartphone|iemobile/i")) {
				return TypeDeviceEnum.MOBILE;
			}
			if(userAgent.matches("/ipad|android 3|sch-i800|playbook|tablet|kindle|gt-p1000|sgh-t849|shw-m180s|a510|a511|a100|dell streak|silk/i")) {
				return TypeDeviceEnum.TABLET;
			}
			return TypeDeviceEnum.DESKTOP;
		}
		return TypeDeviceEnum.UNDEFINED;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static Object getResult(Object access) {
		
		if(Util.isNotNull(access)) {
	    	RestResultDto ret = (RestResultDto)access;
	    	if(Util.isNotNull(ret.getSuccess()) && ret.getSuccess()) {
	    		return ret.getData();
	    	}
	    	else if (Util.isNotNull(ret.getError())) {
	    		throw ret.getError();
	    	}
	    	else if(Util.isNotNull(ret.getSuccess()) && !ret.getSuccess() && Util.isNotNull(ret.getData())) {
	    		RestErrorDto error = RestResultDto.getMapper().convertValue(ret.getData(), new TypeReference<RestErrorDto>() {});
	    		throw new AgilizeException(error);
	    	}
	    	else if(!Util.isNotNull(ret.getSuccess()) && !Util.isNotNull(ret.getData()) && !Util.isNotNull(ret.getError())) {
	    		throw new AgilizeException(HttpStatus.NO_CONTENT.ordinal(), ErrorCodeEnum.SERVICE_NOT_PROCESS);
	    	}
		}
	    else {
	    	throw new AgilizeException(HttpStatus.NO_CONTENT.ordinal(), ErrorCodeEnum.SERVICE_NOT_PROCESS);
	    }
		return null;
	}
	
	public static Application getApplication(String nmApp, String serviceToken) {
		
		if(Util.isNotNull(mapApps.get(nmApp))) {
			return mapApps.get(nmApp);
		}
		else {
			//Fixo em Código!
			String url = "http://localhost:8084/agilize/facade/application";
			//String url = "https://agilize-security.herokuapp.com/agilize/facade/application";
			
			Object access = null;
			Map<String, Object> map = new HashMap<String, Object>(1);
			map.put("nmApplication", nmApp);
			map.put("isInternal", Boolean.TRUE);
			access = accessRestService(url, 5, null, RestResultDto.class, serviceToken, map);
			Application app = null;
			if(isNotNull(access)) {
				app = RestResultDto.getMapper().convertValue(access, new TypeReference<Application>() {});
				mapApps.put(nmApp, app);
			}
    		return app;
		}
	}
	
	public static boolean isValidEmail(String email) {
		
		String strPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		if(isNotNull(email)) {
			Pattern pattern = Pattern.compile(strPattern);
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		}
		return false;
	}
	
	public static boolean isValid(String regex, String value) {
		
		if(!isNotNull(regex) || !isNotNull(value)) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static String decodePassword(String passwordEncoded) {
        if (Util.isNotNull(passwordEncoded)) {
            int div = passwordEncoded.lastIndexOf("-");
            if (div > 0) {
                String uuid = passwordEncoded.substring(0, div);
                String passwordCode = passwordEncoded.substring(div + 1);
                int passwordLength = passwordCode.length() / 3;
                int j = uuid.length() - passwordLength;
                while (j < 0) {
                    j = j + uuid.length();
                }
                StringBuilder password = new StringBuilder(passwordLength);
                for (int i = 0; i < passwordCode.length(); i = i + 3) {
                    if (j > uuid.length()) {
                        j = 0;
                    }
                    password.append((char) (Integer.parseInt(passwordCode.substring(i, i + 3)) - (int) uuid.charAt(j)));
                    j++;
                }
                return password.toString();
            }
        }
        // No valid encoded password.
        return null;
    }	
	
	public static PasswordEncoder getPasswordEncoder() {
		return passwEncoder;
	}
	
	/*public static void main(String[] args) {
		System.out.println("Retorno = "+isValid(IFrameEnum.PATTERN_PHONE_9_DIGITS, "(22) 98888-9099"));
	}*/
}
