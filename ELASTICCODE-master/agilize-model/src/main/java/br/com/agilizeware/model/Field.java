package br.com.agilizeware.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.agilizeware.enums.FieldEnum;
import br.com.agilizeware.enums.RelationShipEnum;

@javax.persistence.Entity
@Table(name="field", catalog="service_agilize")
public class Field implements EntityIf { 
	
	private static final long serialVersionUID = -5654193726923517803L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@ManyToOne
	@JoinColumn(name = "fk_entity", nullable=false)
	@JsonIgnore
	private Entity entity;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_field", nullable=false)
	private FieldEnum typeField;
	@Column(nullable=false)
	private String name;
	@Column(nullable=true)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "fk_entity_ref", nullable=false)
	private Entity entityRef;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_relation", nullable=false)
	private RelationShipEnum typeRelation;
	
	public Field() {
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

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public FieldEnum getTypeField() {
		return typeField;
	}

	public void setTypeField(FieldEnum typeField) {
		this.typeField = typeField;
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

	public Entity getEntityRef() {
		return entityRef;
	}

	public void setEntityRef(Entity entityRef) {
		this.entityRef = entityRef;
	}

	public RelationShipEnum getTypeRelation() {
		return typeRelation;
	}

	public void setTypeRelation(RelationShipEnum typeRelation) {
		this.typeRelation = typeRelation;
	}
	
	public String getLabel() {
		return this.name;
	}

	
}
