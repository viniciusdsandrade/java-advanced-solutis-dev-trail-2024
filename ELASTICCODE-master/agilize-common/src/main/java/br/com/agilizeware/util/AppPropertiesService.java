package br.com.agilizeware.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class AppPropertiesService {
	
	@Autowired
	Environment env;

	/**
	 * Retorna propriedade String
	 * @param key
	 */
	public String getPropertyString(String key) {
		return env.containsProperty(key) ? env.getProperty(key) : null;
	}

	/**
	 * Retorna propriedade Integer
	 * @param key
	 */
	public Integer getPropertyInteger(String key) {
		return env.containsProperty(key) ? Integer.parseInt(env.getProperty(key)) : null;
	}

	/**
	 * Retorna propriedade Long
	 * @param key
	 */
	public Long getPropertyLong(String key) {
		return env.containsProperty(key) ? Long.parseLong(env.getProperty(key)) : null;
	}

	/**
	 * Retorna propriedade Boolean
	 * @param key
	 */
	public Boolean getPropertyBoolean(String key) {
		return env.containsProperty(key) ? Boolean.parseBoolean(env.getProperty(key)) : null;
	}

}