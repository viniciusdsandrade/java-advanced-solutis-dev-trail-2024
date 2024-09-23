package br.com.agilizeware.dto;

import java.io.Serializable;

import org.apache.commons.codec.language.Soundex;

public class Pojo implements Serializable {
	
	public static final String NM_ENTITY = "_nmEntity";
	public static final String NM_SOUNDEX = "_soundex";
	public static final String NM_ID = "_id";
	
	private static final long serialVersionUID = -8881171958228141084L;
	
	private String id;
    private String nmEntity;
    private String soundex;
    private String jSon;
    private String collection;
    
    private static Soundex s = new Soundex();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSoundex() {
		return this.soundex;
	}

	public void setSoundex(String soundex) {
		this.soundex = soundex;
	}

	public String getNmEntity() {
		return nmEntity;
	}

	public void setNmEntity(String nmEntity) {
		this.nmEntity = nmEntity;
	}

	public String getjSon() {
		return jSon;
	}

	public void setjSon(String jSon) {
		this.jSon = jSon;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}
	
	public static String strSoundex(String sds) {
		if(sds != null && !sds.isEmpty()) {
			return s.encode(sds);
		}
		return null;
	}
	
}
