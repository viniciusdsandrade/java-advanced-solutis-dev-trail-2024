package br.com.agilizeware.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@javax.persistence.Entity
@Table(name="city", catalog="service_agilize")
public class City implements EntityIf { 
	
	private static final long serialVersionUID = 6842719203297322652L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation", nullable = true)
	private Long idUserCreate;
	
	@ManyToOne
	@JoinColumn(name = "fk_uf", nullable=false)
	@JsonIgnore
	private UF uf;
	@Column(nullable=false)
	private String name;
	@Column(nullable=true)
	private String description;
	
	public City() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDtCreate() {
		return dtCreate;
	}
	public void setDtCreate(Date dtCreate) {
		this.dtCreate = dtCreate;
	}
	public Long getIdUserCreate() {
		return idUserCreate;
	}
	public void setIdUserCreate(Long idUserCreate) {
		this.idUserCreate = idUserCreate;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLabel() {
		return this.name;
	}

}
