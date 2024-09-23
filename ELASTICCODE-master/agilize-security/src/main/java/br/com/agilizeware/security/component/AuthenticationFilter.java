package br.com.agilizeware.security.component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.model.Application;
import br.com.agilizeware.model.User;
import br.com.agilizeware.security.SecurityConfiguration;
import br.com.agilizeware.security.repository.SecurityDaoImpl;
import br.com.agilizeware.security.repository.UserDaoIf;
import br.com.agilizeware.util.TokenUtils;
import br.com.agilizeware.util.Util;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter extends GenericFilterBean { 
	
	@Autowired
	private UserDaoIf userService;
	@Autowired
	private SecurityDaoImpl securityDaoIf;
	
	private static final Logger log = LogManager.getLogger(AuthenticationFilter.class); 
	private static final String PATH_FACADE = "/agilize/facade"; 
	
	private static final List<String> ALLOW_ACCESS = Arrays.asList(
	        "/", "/agilize/login/authenticate",
	        "/index.html",
	        "/css/bootstrap.css",
	        "/lib/jquery/jquery.js",
	        "/lib/angular/angular-route-1.4.8.min.js",
	        "/lib/css/bootstrap.js",
	        "/lib/angular/angular-locale_pt-br.js",
	        "/lib/angular/angular.js",
	        "/lib/css/ui-bootstrap-tpls.js",
	        "/lib/angular/angular-uuid2.min.js",
			"/lib/common.js", "/lib/services/i18nNotifications.js", "/lib/services/i18nNotifications.js",
			"/lib/services/notifications.js", "/lib/services/httpRequestTracker.js",
			"/lib/services/localizedMessages.js", "/lib/services/authenticationModule.js",
			"/lib/services/authorization.js", "/lib/services/propriedadesCompartilhadas.js",
			"/lib/resources/angular-msgbox.js", "/lib/resources/rest-resource.js", "/lib/resources/devicecheck.js",
			"/js/app.js", "/js/appmsg.js", "/js/controller/login.js", "/css/bootstrap.css.map", "/js/views/login.html",
			"/lib/angular/angular-route.min.js.map", "/img/SB-admin.png", "/auth/google", "/signin", "/signin/google", 
			"/css/style.css", "/css/font-awesome.css", "/lib/directives/backgroundCss.js", 
			"/fonts/fontawesome-webfont.woff2", "/fonts/fontawesome-webfont.woff", "/fonts/fontawesome-webfont.ttf"
	    );

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		log.info("AuthenticationFilter | Security ");
		
		// TODO Auto-generated method stub
		if (!(request instanceof HttpServletRequest)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest)request; 
		
        log.info("Security | AuthenticationFilter | getServletPath = "+httpRequest.getServletPath());

        //Logar o body
		/*BufferedReader responseReader = httpRequest.getReader();
		StringBuilder responseBuilder = new StringBuilder();
		String line;
		if(Util.isNotNull(responseReader)) {
			while ((line = responseReader.readLine()) != null) {
				responseBuilder.append(line);
			}
		}
        log.info("BODY  = ["+responseBuilder+" ]");*/
		
		HttpServletResponse res = (HttpServletResponse) response;       
        res.setHeader("Access-Control-Allow-Origin", "*"); 
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Access-Control-Allow-Headers, Content-Range, Content-Disposition, Content-Type, Accept, X-Header-Application, X-Header-Path, X-Auth-Token, Authorization");

        
        String authToken = httpRequest.getHeader(SecurityConfiguration.TOKEN);
		String serviceToken = httpRequest.getHeader(Util.HEADER_SERVICE_KEY);
        
		if (!ALLOW_ACCESS.contains(httpRequest.getServletPath())) {
			
			if(PATH_FACADE.equals(httpRequest.getServletPath())) {
				String path = httpRequest.getHeader(Util.HEADER_PATH_KEY); 
				String aplication = httpRequest.getHeader(Util.HEADER_APPLICATION_KEY); 
				if(Util.isNotNull(aplication) && Util.isNotNull(path)) {
					Application app = securityDaoIf.findCompleteApplicationByName(aplication);
					if(Util.isNotNull(app.getPathsFree())) {
						String paths[] = app.getPathsFree().split(";");
						for(String p : paths) {
							if(path.equals(p)) {
								//Sucesso
								UsernamePasswordAuthenticationToken authApp = new UsernamePasswordAuthenticationToken(
										new User(app.getName(), app.getPassword()), app.getPassword());
								authApp.setDetails(new WebAuthenticationDetailsSource()
										.buildDetails((HttpServletRequest) request));
								SecurityContextHolder.getContext().setAuthentication(authApp);	
							}
						}
					}
				}
			}
			
			
			if(Util.isNotNull(serviceToken)) {
				String[] parts = serviceToken.split(":");
				Application app = securityDaoIf.findCompleteApplicationByName(parts[0]);
				if(Util.isNotNull(app) && app.getPassword().equals(parts[1])) {
					//Sucesso
					UsernamePasswordAuthenticationToken authApp = new UsernamePasswordAuthenticationToken(
							new User(parts[0], parts[1]), parts[1]);
					authApp.setDetails(new WebAuthenticationDetailsSource()
							.buildDetails((HttpServletRequest) request));
					SecurityContextHolder.getContext().setAuthentication(authApp);
				}
			}
			else if(Util.isNotNull(authToken)) {
				String userName = TokenUtils.getUserNameFromToken(authToken);
				if (userName != null) {
					UserDetails userDetails = this.userService.loadUserByUsername(userName);
					if(userDetails != null) {
						if (TokenUtils.validateToken(authToken, userDetails)) {
							UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
									userDetails, null, userDetails.getAuthorities());
							authentication.setDetails(new WebAuthenticationDetailsSource()
									.buildDetails((HttpServletRequest) request));
							SecurityContextHolder.getContext().setAuthentication(
									authentication);
						}
					}
					else {
						throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.BAD_CREDNTIALS);
					}
				}			
			}
        }
		
		if (!httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
			log.info("FIM **** AuthenticationFilter | doFilter ");
            chain.doFilter(request, response);
        }
		else {
			log.info("FIM **** AuthenticationFilter | OPTIONS");
			res.setStatus(HttpServletResponse.SC_OK);
		}
		
	}
}
