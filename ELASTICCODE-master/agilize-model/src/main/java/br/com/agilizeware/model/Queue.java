package br.com.agilizeware.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.agilizeware.dto.RestResultDto;
import br.com.agilizeware.enums.OrderQueueEnum;
import br.com.agilizeware.enums.QueueEnum;

@javax.persistence.Entity
//@Table(name="queue", catalog="asyncronous_agilize")
@Table(name="queue", catalog="wkcj3dxf55vbvn4w")
public class Queue implements EntityIf { 
	
	private static final long serialVersionUID = -5075170156734612326L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation", nullable = false)
	private Long idUserCreate;
	
	@Column(name="name_entity")
	private String nmEntity;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "fk_application", nullable=false)
	private Application application;
	@Column(nullable=false)
	private QueueEnum queue;
	@Column
	private String url;
	@JsonIgnore
	@Column(name="parameters")
	private String jSonParameters;
	@Column(nullable=false)
	private OrderQueueEnum priority = OrderQueueEnum.MINIMAL;
	@Column(nullable=false)
	private Boolean historic = false;
	@Column(nullable=false)
	private Integer count = 0;
	@Column(nullable=false)
	private Boolean executed = false;
	@Column
	private String error;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_execution")
	private Date dtExecution;
	@Transient
	private Map<String, Object> mapParameters;
	
	public Queue() {
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

	public String getNmEntity() {
		return nmEntity;
	}

	public void setNmEntity(String nmEntity) {
		this.nmEntity = nmEntity;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public QueueEnum getQueue() {
		return queue;
	}

	public void setQueue(QueueEnum queue) {
		this.queue = queue;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public OrderQueueEnum getPriority() {
		return priority;
	}

	public void setPriority(OrderQueueEnum priority) {
		this.priority = priority;
	}

	public Boolean getHistoric() {
		return historic;
	}

	public void setHistoric(Boolean historic) {
		this.historic = historic;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Boolean getExecuted() {
		return executed;
	}

	public void setExecuted(Boolean executed) {
		this.executed = executed;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Date getDtExecution() {
		return dtExecution;
	}

	public void setDtExecution(Date dtExecution) {
		this.dtExecution = dtExecution;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapParameters() {
		if(mapParameters != null && !mapParameters.isEmpty()) {
			return mapParameters;
		}
		else if(jSonParameters != null && !jSonParameters.isEmpty()) {
			try {
				return RestResultDto.getMapper().readValue(jSonParameters, HashMap.class);
			}
			catch(IOException ioe) { /*Abafada*/ }
		}
		return null;
	}

	public void setMapParameters(Map<String, Object> mapParameters) {
		this.mapParameters = mapParameters;
	}

	public String getjSonParameters() {
		return jSonParameters;
	}

	public void setjSonParameters(String jSonParameters) {
		this.jSonParameters = jSonParameters;
	}
	
	public String getLabel() {
		return "";
	}

	
}
