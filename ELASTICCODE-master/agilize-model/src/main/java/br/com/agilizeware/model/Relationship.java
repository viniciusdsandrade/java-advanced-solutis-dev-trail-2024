package br.com.agilizeware.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.agilizeware.enums.RelationShipEnum;

@javax.persistence.Entity
@Table(name="relationship", catalog="service_agilize")
public class Relationship implements EntityIf {
	
	private static final long serialVersionUID = -5120831237979659555L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@OneToOne
	@JoinColumn(name = "fk_entity_father", nullable=false)
	private Entity father;
	@OneToOne
	@JoinColumn(name = "fk_entity_children", nullable=false)
	private Entity children;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_relation", nullable=false)
	private RelationShipEnum typeRelation;
	@Column
	private String description;
	
	public Relationship() {
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

	public Entity getFather() {
		return father;
	}

	public void setFather(Entity father) {
		this.father = father;
	}

	public Entity getChildren() {
		return children;
	}

	public void setChildren(Entity children) {
		this.children = children;
	}

	public RelationShipEnum getTypeRelation() {
		return typeRelation;
	}

	public void setTypeRelation(RelationShipEnum typeRelation) {
		this.typeRelation = typeRelation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLabel() {
		return this.description;
	}

}
