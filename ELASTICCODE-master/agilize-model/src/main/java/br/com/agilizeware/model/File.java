package br.com.agilizeware.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@javax.persistence.Entity
@Table(name="file", catalog="file_agilize")
public class File implements EntityIf { 
	
	private static final long serialVersionUID = 5248370222029141711L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@Column(name="name_entity", nullable=false)
	private String nmEntity;
	@Column(nullable=false)
	private String name;
	@Column(name="path_physical", nullable=false)
	private String pathPhysical;
	@Column(name="path_logical", nullable=true)
	private String pathLogical;
	
	@Column(name="content_type", nullable=true)
	private String contentType;
	
	public File() {
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

	public String getPathPhysical() {
		return pathPhysical;
	}

	public void setPathPhysical(String pathPhysical) {
		this.pathPhysical = pathPhysical;
	}

	public String getPathLogical() {
		return pathLogical;
	}

	public void setPathLogical(String pathLogical) {
		this.pathLogical = pathLogical;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getNmEntity() {
		return nmEntity;
	}

	public void setNmEntity(String nmEntity) {
		this.nmEntity = nmEntity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		return this.name;
	}

	
}
