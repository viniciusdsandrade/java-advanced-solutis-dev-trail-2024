package br.com.agilizeware.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="field_value", catalog="service_agilize")
public class FieldValue implements EntityIf {
	
	private static final long serialVersionUID = 3132091359564463340L;
	
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
	@JoinColumn(name = "fk_field", nullable=false)
	private Field field;
	@Column
	private String value;
	
	public FieldValue() {
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
	
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return "";
	}

}
