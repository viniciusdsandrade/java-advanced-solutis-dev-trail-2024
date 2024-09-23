package br.com.agilizeware.dto;

public class SortDTO extends DtoAb {

	private static final long serialVersionUID = 1L;

	public SortDTO() {
	}

	public SortDTO(String name, Boolean asc) {
		super();
		this.name = name;
		this.asc = asc;
	}

	private String name;
	private Boolean asc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getAsc() {
		return asc != null ? asc : true;
	}

	public void setAsc(Boolean asc) {
		this.asc = asc;
	}
	
	public String getNmEntity() {
		return null;
	}
}
