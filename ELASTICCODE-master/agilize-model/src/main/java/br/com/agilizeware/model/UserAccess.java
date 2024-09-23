package br.com.agilizeware.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.agilizeware.enums.TypeDeviceEnum;


@Entity
//@Table(name="user_access", catalog="agilize_security")
@Table(name="user_access", catalog="tv8lo3nb8etoit7u")
public class UserAccess implements EntityIf {
	
	private static final long serialVersionUID = -1003591302573409129L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_device", nullable=false)
	private TypeDeviceEnum typeDevice;
	
	
	public UserAccess() {
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


	public TypeDeviceEnum getTypeDevice() {
		return typeDevice;
	}


	public void setTypeDevice(TypeDeviceEnum typeDevice) {
		this.typeDevice = typeDevice;
	}
	
	public String getLabel() {
		return "";
	}

	
}
