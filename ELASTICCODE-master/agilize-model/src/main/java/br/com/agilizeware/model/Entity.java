package br.com.agilizeware.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@javax.persistence.Entity
@Table(name="entity", catalog="service_agilize")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class Entity implements EntityIf { 
	
	
	private static final long serialVersionUID = -3839097455972867137L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@Column(nullable=false)
	private String name;
	@Column
	private String description;
	@OneToMany(mappedBy="entity", fetch = FetchType.EAGER, cascade=CascadeType.REMOVE)
	private List<Field> fields;
	
	/*@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinTable(name = "entity_application", catalog="service_agilize", joinColumns = {
			@JoinColumn(name = "fk_entity", insertable=false, updatable=false) },
			inverseJoinColumns = { @JoinColumn(name = "fk_application", insertable=false, updatable=false) })
	private Set<Application> applications;*/
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy="entity")
	private Set<EntityApplication> applications;
	
	public Entity() {
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
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public Set<EntityApplication> getApplications() {
		return applications;
	}

	public void setApplications(Set<EntityApplication> applications) {
		this.applications = applications;
	}

	public String getLabel() {
		return this.name;
	}

}
