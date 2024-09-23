package br.com.agilizeware.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 
 * Nome: DateUtil.java Propósito:
 * <p>
 * Obter data e tratar funções de data do sistema.
 * </p>
 * 
 * @author Gestum / LMS <BR/>
 *         Equipe: Gestum - Software -São Paulo <BR>
 * @version: 1.7
 *
 *           Registro de Manutenção: 30/03/2015 18:31:23 - Autor: Tiago de
 *           Almeida Lopes - Responsável: Regis Neves - Criação
 */
public final class DateUtil {

	/**
	 * Atributo locale.
	 */
	public static Locale locale = LocaleContextHolder.getLocale();

	/**
	 * Contrutor padrão da classe. Privado para não instânciar essa classe.
	 */
	private DateUtil() {
		super();
	}

	/**
	 * 
	 * Devolve a data atual.
	 * 
	 * @return Data atual.
	 */
	public static Date dataAtualHora() {
		return Calendar.getInstance(locale).getTime();
	}

	/**
	 * 
	 * Adiciona mais unidades na data atual para o field informado que pode ser,
	 * data, hora, minuto, segundo, mês, ano. EX de uso:
	 * <i>plus(Calendar.SECOND, 1)</i>
	 * 
	 * @param field
	 *            Campo da data que deseja adicionar mais unidades.
	 * 
	 * @param value
	 *            Quantidade que deseja acrescer o campo informado na data
	 *            atual.
	 * 
	 * @return Nova data acrescida do valor do campo informado.
	 */
	public static Date plus(Integer field, Integer value) {
		Calendar calen = Calendar.getInstance(locale);
		calen.setTime(dataAtualHora());
		calen.add(field, value);

		return calen.getTime();
	}

	/**
	 * Retorna o valor do horário minimo para a data de referencia passada. <BR>
	 * <BR>
	 * Por exemplo se a data for "30/01/2009 as 17h:33m:12s e 299ms" a data
	 * retornada por este metodo será "30/01/2009 as 00h:00m:00s e 000ms".
	 * 
	 * @param date
	 *            de referencia.
	 * @return {@link Date} que representa o horário minimo para dia informado.
	 */
	public static Date lowDateTime(Date date) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(date);
		aux = toOnlyDate(aux); // zera os parametros de hour,min,sec,milisec
		return aux.getTime();
	}

	/**
	 * Retorna o valor do horário maximo para a data de referencia passada. <BR>
	 * <BR>
	 * Por exemplo se a data for "30/01/2009 as 17h:33m:12s e 299ms" a data
	 * retornada por este metodo será "30/01/2009 as 23h:59m:59s e 999ms".
	 * 
	 * @param date
	 *            de referencia.
	 * @return {@link Date} que representa o horário maximo para dia informado.
	 */
	public static Date highDateTime(Date date) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(date);
		aux = toOnlyDate(aux); // zera os parametros de hour,min,sec,milisec
		aux.add(Calendar.DATE, 1); // vai para o dia seguinte
		return DateUtils.addMilliseconds(aux.getTime(), -1000); // reduz 1 milisegundo
	}

	/**
	 * Zera todas as referencias de hora, minuto, segundo e milesegundo do
	 * {@link Calendar}.
	 * 
	 * @param date
	 *            a ser modificado.
	 */
	public static Calendar toOnlyDate(Calendar date) {
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		
		return date;
	}

	/**
	 * Zera todas as referencias de dia, mês e ano do
	 * {@link Calendar}.
	 * 
	 * @param date
	 *            a ser modificado.
	 */
	public static void toOnlyHour(Calendar date) {
		date.set(Calendar.DAY_OF_MONTH, 0);
		date.set(Calendar.MONTH, 0);
		date.set(Calendar.YEAR, 0);
	}
	
	/**
	 * 
	 * Descobra se a data e hora atual esta dentro do intervalo informado de
	 * data e hora. Treu caso a data e hora atual estiver dentro do intervalo
	 * informado, false do contrário.
	 * 
	 * @param dthourStart
	 *            Data e hora de início da checagem.
	 * @param dthourEnd
	 *            Data e hora de fim da checagem.
	 * @return True caso a data e hora atual esteja no intervalo, false do
	 *         contrário.
	 */
	public static Boolean hasDataHourActualBetweenInterval(Date dthourStart,
			Date dthourEnd) {
		Boolean onInterval = Boolean.FALSE;
		Calendar caleStart = Calendar.getInstance(locale);
		Calendar caleEnd = Calendar.getInstance(locale);
		Calendar caleActualDataHour = Calendar.getInstance(locale);

		if (dthourStart != null && dthourEnd != null) {

			caleStart.setTime(dthourStart);
			caleEnd.setTime(dthourEnd);

			toOnlyHour(caleActualDataHour);
			toOnlyHour(caleStart);
			toOnlyHour(caleEnd);
			
			if(caleEnd.before(caleStart)) {
				caleEnd.add(Calendar.HOUR_OF_DAY, 24);
			}
			
			if (caleActualDataHour.after(caleStart)
					&& caleActualDataHour.before(caleEnd)) {
				onInterval = Boolean.TRUE;
			}
		}

		return onInterval;
	}
	

	  /**
	   * 
	   * Descobra se a data e hora atual esta dentro do intervalo informado de
	   * data e hora. Treu caso a data e hora atual estiver dentro do intervalo
	   * informado, false do contrário.
	   * 
	   * @param dthourStart
	   *            Data e hora de início da checagem.
	   * @param dthourEnd
	   *            Data e hora de fim da checagem.
	   * @return True caso a data e hora atual esteja no intervalo, false do
	   *         contrário.
	   * @throws ParseException 
	   */
	  public static Date parseToDate(String dtToParse) throws ParseException {
	    SimpleDateFormat spdf = new SimpleDateFormat("dd/MM/yyyy");
	    Calendar cale = Calendar.getInstance(locale);

	    cale.setTime(spdf.parse(dtToParse));

	    cale = toOnlyDate(cale);

	    return cale.getTime();
	  }

	  /**
	   * 
	   * Descobra se a data e hora atual esta dentro do intervalo informado de
	   * data e hora. Treu caso a data e hora atual estiver dentro do intervalo
	   * informado, false do contrário.
	   * 
	   * @param dthourStart
	   *            Data e hora de início da checagem.
	   * @param dthourEnd
	   *            Data e hora de fim da checagem.
	   * @return True caso a data e hora atual esteja no intervalo, false do
	   *         contrário.
	   * @throws ParseException 
	   */
	  public static String parseToString(Date dtToParse) throws ParseException {
	    SimpleDateFormat spdf = new SimpleDateFormat("dd/MM/yyyy");
	    return spdf.format(dtToParse);
	  }
	  
}
