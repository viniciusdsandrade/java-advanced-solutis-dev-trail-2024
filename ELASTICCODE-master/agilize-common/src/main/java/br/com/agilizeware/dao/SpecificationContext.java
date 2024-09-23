package br.com.agilizeware.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;

/***
 * 
 * Nome: SpecificationContext.java Propósito:
 * <p>
 * Contexto da especificação.
 * </p>
 * 
 * @author Gestum / LMS <BR/>
 *         Equipe: Gestum - Software -São Paulo <BR>
 * @version: 1.7
 * 
 *           Registro de Manutenção: 27/03/201417:42:37 - Autor: Tiago de
 *           Almeida Lopes - Responsável: Thiago Monteiro - Criação.
 */
public class SpecificationContext {

	/**
	 * Atributo relationPathCache.
	 */
	public Map<String, Path> relationPathCache = new HashMap<String, Path>();

	/**
	 * Atributo relationJoinCache.
	 */
	public Map<String, From> relationJoinCache = new HashMap<String, From>();

}
