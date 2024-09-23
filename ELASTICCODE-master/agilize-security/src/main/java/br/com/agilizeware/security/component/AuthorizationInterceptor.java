package br.com.agilizeware.security.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.security.repository.SecurityDaoImpl;
import br.com.agilizeware.util.Util;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
	
	@Autowired
	private SecurityDaoImpl securityDaoIf;
	
	private static List<String> urlsAllowed = new ArrayList<String>(1);
	
	static {
		urlsAllowed.add("/agilize/login/authenticate");
		urlsAllowed.add("/agilize/facade/enums"); 
		urlsAllowed.add("/agilize/facade/applications");
		urlsAllowed.add("/agilize/login/validateToken");
		urlsAllowed.add("/agilize/facade/application");
		urlsAllowed.add("/agilize/login/google");
		urlsAllowed.add("/agilize/login");
		urlsAllowed.add("/security/login/");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		if(urlsAllowed.contains(request.getServletPath())) {
			return true;
		}
		
		String path = request.getHeader(Util.HEADER_PATH_KEY); 
		String aplication = request.getHeader(Util.HEADER_APPLICATION_KEY); 
		if(!Util.isNotNull(path)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.PATH_NULL);
		}
		if(!Util.isNotNull(aplication)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.APPLICATION_NULL);
		}
		
		Collection<SimpleGrantedAuthority> authorities = securityDaoIf.findRolesForPathApplication(aplication, path.replaceAll("[0-9]", ""), Util.getHttpMethod(request.getMethod()));
		//Se não estiver vazio, validar permissão
		if(authorities != null && !authorities.isEmpty()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(!contains(authentication.getAuthorities(), authorities)) {
				throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_PATH);
			}
		}
		
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
	}
	
	private static boolean contains(Collection<? extends GrantedAuthority> c1, Collection<SimpleGrantedAuthority> c2) {
		if(((c1 != null && !c1.isEmpty()) && (c2 == null || c2.isEmpty())) || (((c1 == null || c1.isEmpty())) && ((c2 != null && !c2.isEmpty())))) {
			return false;
		}
		for(GrantedAuthority s1 : c1) {
			for(SimpleGrantedAuthority s2 : c2) {
				if(s1.getAuthority().equals(s2.getAuthority())) {
					return true;
				}
			}
		}
		return false;
	}
}
