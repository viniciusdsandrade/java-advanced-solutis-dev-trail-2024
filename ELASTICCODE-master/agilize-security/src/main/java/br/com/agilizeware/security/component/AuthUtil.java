package br.com.agilizeware.security.component;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.enums.TypeAuthenticationEnum;
import br.com.agilizeware.enums.TypeDeviceEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Profile;
import br.com.agilizeware.model.User;
import br.com.agilizeware.model.UserAccess;
import br.com.agilizeware.security.repository.UserDaoIf;
import br.com.agilizeware.util.SaltedSHA256PasswordEncoder;
import br.com.agilizeware.util.TokenUtils;
import br.com.agilizeware.util.Util;

public class AuthUtil {

	@Autowired
    private UserDaoIf userDaoIf;
	@Autowired
	private AuthenticationManager authManager;

	private static final Logger log = LoggerFactory.getLogger(AuthUtil.class);

    /*public static void authenticate(Connection<?> connection) {
        UserProfile userProfile = connection.fetchUserProfile();
        String username = userProfile.getUsername();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User {} {} connected.", userProfile.getFirstName(), userProfile.getLastName());
    }*/
	
	public AuthUtil() {
		super();
	}
    
	@Transactional(isolation=Isolation.READ_COMMITTED, rollbackFor=Exception.class)
    public User authenticProfile(Connection<?> connection, TypeDeviceEnum typeDevice) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(connection.fetchUserProfile(), profile);
        String userId = UUID.randomUUID().toString();
        profile.setImageUrl(connection.getImageUrl());
        profile.setDevice(typeDevice);
        
        log.info("Obtendo Profile: " + profile.getUsername());
        Profile p = null;
        try {
            p = userDaoIf.findProfileByUserName(profile.getUsername());
            p.setDevice(typeDevice);
        }
        catch(AgilizeException ae) {
        	if(ErrorCodeEnum.BAD_CREDNTIALS.equals(ae.getErrorCodeEnum())) {
        		//Abafar
        	}
        	else {
        		throw ae;
        	}
        }
        User us = null;
        if(Util.isNotNull(p) && Util.isNotNull(p.getUserId())) {
        	log.info("Autenticando Profile Existente");
        	us = authenticate(p, null);
        }
        else {
            log.info("Criando profile: " + userId);
            userDaoIf.createUser(userId, profile);
            log.info("Autenticando Profile Criado");
            us = authenticate(profile, null);
        }
        
        return us;
    }
    
    public User authenticate(User user, String passw) {

		String username = user.getUsername(); 
		if(TypeAuthenticationEnum.CPF.equals(user.getTypeAuthentication())) {
			username = Util.onlyNumbers(username);
		}
		if(!Util.isNotNull(passw)) {
			passw =  user.getPassword();
			passw = Util.decodePassword(user.getPassword());
			if(Util.isNotNull(passw)) {
				passw = Util.getPasswordEncoder().encode(passw);
			}
		}
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				username, passw);
		Authentication authentication = null;
		
		try {
			authentication = this.authManager.authenticate(authenticationToken);
		}catch(BadCredentialsException bce) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS, bce);
		}catch (CredentialsExpiredException e) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
		}catch(DisabledException dis) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.USER_DISABLED);
		}catch(InternalAuthenticationServiceException iase) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS, iase);
		}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User userlogged = (User) authentication.getPrincipal();
		
		//TODO: Realizar a implementação das validações de acesso e regras pertinentes
		
		//Persisti a informação de acesso do usuario
		//if(access == null || access != 2) {
			UserAccess ua = new UserAccess();
			ua.setDtCreate(Util.obterDataHoraAtual());
			ua.setIdUserCreate(userlogged.getId());
			ua.setTypeDevice(Util.isNotNull(user.getDevice()) ? user.getDevice() : TypeDeviceEnum.UNDEFINED);
			userDaoIf.saveUserAccess(ua);
		//}
		
		userlogged.setToken(TokenUtils.createToken(userlogged));
		userlogged.setPassword(null);
		return userlogged;
	}
}
