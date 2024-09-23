package br.com.agilizeware.model;

import java.util.Date;
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@javax.persistence.Entity
//@Table(name="application", catalog="agilize_security")
@Table(name="application", catalog="tv8lo3nb8etoit7u")
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
public class Application implements EntityIf { 
	
	private static final long serialVersionUID = 6965155756868625126L;
	
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
	@Column(nullable=false)
	private String host;
	@Column(name="password_service", nullable=false)
	private String password;
	@Column(nullable=false)
	private Boolean active;
	
	/*@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy="applications")
	public Set<Entity> entities;*/
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy="application")
	@JsonIgnore
	public Set<EntityApplication> entities;
	
	@Column(name="flg_environment_pay_prod", nullable=false)
	private Boolean flgEnvironmentPayProd;
	@Column(name="merchant_order_id")
	private String merchantId;
	@Column(name="merchant_order_key")
	private String merchantKey;
	@Column(name="payment_url_return")
	private String paymentUrlReturn;
	@Column(name="paths_free")
	private String pathsFree;
	@Column(name="url_validation_pre_payment")
	private String urlValidationPrePayment;
	@Column(name="url_process_pos_payment")
	private String urlProcessPosPayment;
	@Column(name="fk_background_image")
	private Long idBackGround;
	@Column(name="title_application")
	private String titleApp;
	
	
	@Transient
	private File backgroundImage;

	public Application() {
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set<EntityApplication> getEntities() {
		return entities;
	}

	public void setEntities(Set<EntityApplication> entities) {
		this.entities = entities;
	}

	public Boolean getFlgEnvironmentPayProd() {
		return flgEnvironmentPayProd;
	}

	public void setFlgEnvironmentPayProd(Boolean flgEnvironmentPayProd) {
		this.flgEnvironmentPayProd = flgEnvironmentPayProd;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getPaymentUrlReturn() {
		return paymentUrlReturn;
	}

	public void setPaymentUrlReturn(String paymentUrlReturn) {
		this.paymentUrlReturn = paymentUrlReturn;
	}

	public String getPathsFree() {
		return pathsFree;
	}

	public void setPathsFree(String pathsFree) {
		this.pathsFree = pathsFree;
	}

	public String getUrlValidationPrePayment() {
		return urlValidationPrePayment;
	}

	public void setUrlValidationPrePayment(String urlValidationPrePayment) {
		this.urlValidationPrePayment = urlValidationPrePayment;
	}

	public String getUrlProcessPosPayment() {
		return urlProcessPosPayment;
	}

	public void setUrlProcessPosPayment(String urlProcessPosPayment) {
		this.urlProcessPosPayment = urlProcessPosPayment;
	}

	public Long getIdBackGround() {
		return idBackGround;
	}

	public void setIdBackGround(Long idBackGround) {
		this.idBackGround = idBackGround;
	}

	public File getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(File backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getTitleApp() {
		return titleApp;
	}

	public void setTitleApp(String titleApp) {
		this.titleApp = titleApp;
	}
	
	public String getLabel() {
		return this.name;
	}

	
}