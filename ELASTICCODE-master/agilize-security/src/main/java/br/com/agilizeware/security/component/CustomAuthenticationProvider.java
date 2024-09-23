package br.com.agilizeware.security.component;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.User;
import br.com.agilizeware.security.repository.SecurityDaoImpl;
import br.com.agilizeware.security.repository.UserDaoIf;
import br.com.agilizeware.util.Util;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserDaoIf userService;
	@Autowired
	private SecurityDaoImpl securityDaoIf;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = authentication.getName();
		String password = Util.isNotNull(authentication.getCredentials()) ? (String) authentication.getCredentials() : null;

		User user = null;
		
		try {
			user = userService.loadUserByUsername(username);
		}catch(AgilizeException age) {
			if(ErrorCodeEnum.BAD_CREDNTIALS.equals(age.getErrorCodeEnum())) {
				user = null;
			}
			else {
				throw age;
			}
		}
		
		//Verificando se a autenticação não é por serviço
		if(!Util.isNotNull(user)) {
			Application app = securityDaoIf.findCompleteApplicationByName(username);
			if(Util.isNotNull(app) && app.getPassword().equals(password)) {
				User usApp = new User(username, password);
				return new UsernamePasswordAuthenticationToken(usApp, password);
			}
		}
		
		if (user == null || !user.getUsername().equalsIgnoreCase(username)) {
			// throw new BadCredentialsException("Username not found.");
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
		}
		if ((Util.isNotNull(password) && Util.isNotNull(user.getPassword()) && !password.equals(user.getPassword())) || 
				(!Util.isNotNull(password) && Util.isNotNull(user.getPassword())) ||
				(Util.isNotNull(password) && !Util.isNotNull(user.getPassword()))) {
			// throw new BadCredentialsException("Wrong password.");
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.WRONG_PASSWORD);
		}
		if (!user.getAccountNonExpired() || !user.getCredentialsNonExpired()) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.EXPIRED_ACCOUNT);
		}
		if (!user.getAccountNonLocked()) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.LOCKED_ACCOUNT);
		}
		if ((Util.isNotNull(user.getDtRemove()) && user.getDtRemove().before(Util.obterDataHoraAtual()))
				|| !user.getEnabled()) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.INATIVE_ACCOUNT);
		}

		// TODO: Implementar a questão das regras X autorização
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

		return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	public boolean supports(Class<?> arg0) {
		return true;
	}
}
