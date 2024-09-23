package br.com.agilizeware.security.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.agilizeware.dao.DaoAB;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Profile;
import br.com.agilizeware.model.User;
import br.com.agilizeware.model.UserAccess;
import br.com.agilizeware.util.Util;

@Component("userDaoIf")
public class UserDaoImpl extends DaoAB<User, Long> implements UserDaoIf {

	@Autowired
	private UserAccessRepository uaRepository;
	@Autowired
	private SecurityDaoImpl securityDaoIf;
	
	@Autowired
	public UserDaoImpl(UserDaoRepository repository) {
		super(User.class, repository);
	}
	
	/*@Autowired
	public UserDaoImpl(UserDaoRepository repository, UserAccessRepository uaRepository) {
		super(User.class, repository);
		this.repository = repository;
		this.uaRepository = uaRepository;
	}*/
	
	@SuppressWarnings("unchecked")
	@Override
	public User loadUserByUsername(String username) {
		
		User user = ((UserDaoRepository)getRepositorio()).loadUserByUsername(username);
		if(user != null && user.getId() != null) {
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(1);
			authorities = Collections.synchronizedCollection(authorities);
			if(Util.isNotNull(user) && Util.isNotNull(user.getId())) {
				List<Object[]> roles = securityDaoIf.findRolesForUser(user.getId());
				if(Util.isListNotNull(roles)) {
					for(Object[] obj : roles) {
						authorities.add(new SimpleGrantedAuthority(obj[1].toString()));
					}
				}
			}
			user.setAuthorities(authorities); //Collections.unmodifiableCollection(authorities));
			return user;
		}
		throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
	}

	public UserAccess saveUserAccess(UserAccess ua) {
		return uaRepository.save(ua);
	}
	
	public List<User> findUsersAdm() {
		return ((UserDaoRepository)getRepositorio()).findUsersAdm(Util.obterDataHoraAtual());
	}
	
	public User retrieveUserLogged() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		if ("anonymousUser".equals(ctx.getAuthentication().getPrincipal())) {
			return null;
		}
		UserDetails loggedUser = (UserDetails) ctx.getAuthentication()
				.getPrincipal();
		return (User)loggedUser;
	}
	
	
	@SuppressWarnings("unchecked")
	public Profile findProfileByUserName(String login) {
		Profile user = ((UserDaoRepository)getRepositorio()).loadUserProfileByLogin(login);
		if(user != null && user.getId() != null) {
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(1);
			authorities = Collections.synchronizedCollection(authorities);
			if(Util.isNotNull(user) && Util.isNotNull(user.getId())) {
				List<Object[]> roles = securityDaoIf.findRolesForUser(user.getId());
				if(Util.isListNotNull(roles)) {
					for(Object[] obj : roles) {
						authorities.add(new SimpleGrantedAuthority(obj[1].toString()));
					}
				}
			}
			user.setAuthorities(authorities); //Collections.unmodifiableCollection(authorities));
			return user;
		}
		throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
	}
	
	public List<User> findAll() {
		return ((UserDaoRepository)getRepositorio()).findAll(Util.obterDataHoraAtual());
	}
	
	public void createUser(String userId, Profile profile) {
		profile.setUserId(userId);
		profile.setDtCreate(Util.obterDataHoraAtual());
		profile.setAccountNonExpired(true);
		profile.setAccountNonLocked(true);
		profile.setCredentialsNonExpired(true);
		profile.setEnabled(true);
		profile.setSuperAdm(false);
		
		((UserDaoRepository)getRepositorio()).save(profile);
	}
	
	public void delete(Long id) {
		
		Query querie = em.createQuery("delete from UserAccess usa where usa.idUserCreate = "+id);
		querie.executeUpdate();
		super.delete(id);
	}
	
}
