package br.com.agilizeware.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.agilizeware.enums.TypeAuthenticationEnum;
import br.com.agilizeware.enums.TypeDeviceEnum;


@Entity
//@Table(name="user", catalog="agilize_security")
@Table(name="user", catalog="tv8lo3nb8etoit7u")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails, EntityIf {
	
	
	private static final long serialVersionUID = -1840962478776756430L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_creation", nullable = false)
	private Date dtCreate;
	@Column(name="fk_user_creation")
	private Long idUserCreate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dt_remove")
	private Date dtRemove;
	@Column(nullable=false)
	private String name;
	@Column
	private String cpf;
	@Column(name="dt_birth")
	private Date dtNascimento;
	@Column
	private String password;
	@Column(nullable=false)
	private String username;
	@Column(name="non_expired", nullable=false)
	private Boolean accountNonExpired = true;
	@Column(name="non_locked", nullable=false)
	private Boolean accountNonLocked = true;
	@Column(name="credential_non_expired", nullable=false)
	private Boolean credentialsNonExpired = true;
	@Column(name="enabled", nullable=false)
	private Boolean enabled = true;
	@Column(name="is_super_adm", nullable=false)
	private Boolean superAdm = false;
    private String email;
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.DETACH)
	@JoinColumn(name = "fk_file")
	private File file;

	@Transient
	private TypeDeviceEnum device;
	@Transient
	private TypeAuthenticationEnum typeAuthentication;
	@Transient
	private Collection<SimpleGrantedAuthority> authorities;
	@Transient
	private String token;
	
	public User() {
		super();
	}
	
	public User(String userName, String passWord) {
		super();
		this.username = userName;
		this.password = passWord;
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

	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(authorities != null && !authorities.isEmpty()) {
			return Collections.unmodifiableCollection(this.authorities);
		}
		return null;
	}
	
	public void setAuthorities(Collection<SimpleGrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return getAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return getAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return getCredentialsNonExpired();
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return getEnabled();
	}

	public TypeDeviceEnum getDevice() {
		return device;
	}

	public void setDevice(TypeDeviceEnum device) {
		this.device = device;
	}

	public TypeAuthenticationEnum getTypeAuthentication() {
		return typeAuthentication;
	}

	public void setTypeAuthentication(TypeAuthenticationEnum typeAuthentication) {
		this.typeAuthentication = typeAuthentication;
	}

	public Date getDtRemove() {
		return dtRemove;
	}

	public void setDtRemove(Date dtRemove) {
		this.dtRemove = dtRemove;
	}

	public Boolean getSuperAdm() {
		return superAdm;
	}

	public void setSuperAdm(Boolean superAdm) {
		this.superAdm = superAdm;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String getLabel() {
		return this.name;
	}

	
}
