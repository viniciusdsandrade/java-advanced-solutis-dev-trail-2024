package br.com.agilizeware.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.com.agilizeware.enums.BrandEnum;
import br.com.agilizeware.enums.StatusPaymentEnum;
import br.com.agilizeware.enums.TypeCardEnum;
import cieloecommerce.sdk.ecommerce.Sale;

@Entity
//@Table(name="payment", catalog="payment_agilize")
@Table(name="payment", catalog="y8rzc1albyu9sfq8")
public class Payment implements EntityIf {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6524620875861013570L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "fk_application", nullable=false)
	private Application application;
	@Column(name="amount", nullable = false)
	private BigDecimal value;
	@Column(name="customer_name", nullable = false)
	private String customer;
	@Column(name = "type_card", nullable=false)
	private TypeCardEnum typeCard;
	@Column(name = "card_number", nullable=false)
	private String cardNumber;
	@Column(name = "name_at_card", nullable=false)
	private String nameAtCard;
	@Column(name = "month_expiration_date_card", nullable=false)
	private Integer monthExpirationDateCard;
	@Column(name = "year_expiration_date_card", nullable=false)
	private Integer yearExpirationDateCard;
	@Column(name = "security_code_card", nullable=false)
	private String securityCodeCard;
	@Column(name = "brand", nullable=false)
	private BrandEnum brand;
	@Column(name = "merchant_order_id", nullable=false)
	private String merchantOrderId;
	
	@Column(name = "nsu")
	private String proofOfSale;
	@Column(name = "tid")
	private String tid;
	@Column(name = "authorization_code")
	private String authorizationCode;
	@Column(name = "payment_id")
	private String paymentId;
	@Column(name = "eci")
	private String eci;
	@Column(name = "status", nullable=false)
	private StatusPaymentEnum status;
	@Column(name = "debit_url_return")
	private String debitUrlReturn;
	
	@Transient
	private Sale sale;
	/*@Transient
	private String token;*/
	
	public Payment() {}
	
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

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public TypeCardEnum getTypeCard() {
		return typeCard;
	}

	public void setTypeCard(TypeCardEnum typeCard) {
		this.typeCard = typeCard;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getNameAtCard() {
		return nameAtCard;
	}

	public void setNameAtCard(String nameAtCard) {
		this.nameAtCard = nameAtCard;
	}

	public Integer getMonthExpirationDateCard() {
		return monthExpirationDateCard;
	}

	public void setMonthExpirationDateCard(Integer monthExpirationDateCard) {
		this.monthExpirationDateCard = monthExpirationDateCard;
	}

	public Integer getYearExpirationDateCard() {
		return yearExpirationDateCard;
	}

	public void setYearExpirationDateCard(Integer yearExpirationDateCard) {
		this.yearExpirationDateCard = yearExpirationDateCard;
	}

	public String getSecurityCodeCard() {
		return securityCodeCard;
	}

	public void setSecurityCodeCard(String securityCodeCard) {
		this.securityCodeCard = securityCodeCard;
	}

	public BrandEnum getBrand() {
		return brand;
	}

	public void setBrand(BrandEnum brand) {
		this.brand = brand;
	}

	public String getProofOfSale() {
		return proofOfSale;
	}

	public void setProofOfSale(String proofOfSale) {
		this.proofOfSale = proofOfSale;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getEci() {
		return eci;
	}

	public void setEci(String eci) {
		this.eci = eci;
	}

	public StatusPaymentEnum getStatus() {
		return status;
	}

	public void setStatus(StatusPaymentEnum status) {
		this.status = status;
	}

	public String getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public String getDebitUrlReturn() {
		return debitUrlReturn;
	}

	public void setDebitUrlReturn(String debitUrlReturn) {
		this.debitUrlReturn = debitUrlReturn;
	}

	public String getLabel() {
		return "";
	}

}
