package br.com.agilizeware.payment;

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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import br.com.agilizeware.enums.ApplicationNamesEnum;
import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAuthenticationFilter extends GenericFilterBean { 

	private static final Logger log = LogManager.getLogger(ServiceAuthenticationFilter.class);
	public static final String KEY = "ersoqkscmatri82a9IJJU982ssfesnmcnndjesfessncm3D9D9D0F0iiq2sd7jHX";
	public static final String NAME_APP = ApplicationNamesEnum.PAYMENT.getName();
	
	private static List<String> ALLOWEDS = Arrays.asList("/", "/index.html", "/lib/css/bootstrap.js", "/lib/angular/angular.js", 
			"/lib/angular/angular-route-1.4.8.min.js", "/lib/angular/angular-locale_pt-br.js", "/lib/css/ui-bootstrap-tpls.js", 
			"/lib/angular/angular-uuid2.min.js", "/css/bootstrap.css", "/lib/css/ui-bootstrap-tpls.js",
			"/lib/jquery/jquery.js", "/lib/jquery/mask.js", "/lib/services/notifications.js", "/js/lib/jquery/jquery.ui.widget.js", "/lib/common.js",
			"/lib/services/notifications.js", "/lib/services/localizedMessages.js", "/lib/services/httpRequestTracker.js", "/lib/services/authorization.js", 
			"/lib/services/propriedadesCompartilhadas.js", "/lib/services/authenticationModule.js", "/lib/resources/rest-resource.js", 
			"/lib/resources/angular-msgbox.js", "/lib/resources/devicecheck.js", "/js/controller/paymentTran.js", "/js/payment.js", 
			"/js/lib/directive/ngCpfCnpj.js", "/js/resources/paymentTran.js", "/lib/jquery/jquery.mask.js", "/lib/jquery/jquery.ui.widget.js",
			"/lib/directive/ngCpfCnpj.js", "/lib/services/i18nNotifications.js", "/js/appmsg.js", "/css/bootstrap.css.map", 
			"/lib/angular/angular-route.min.js.map", "/lib/services/i18nNotifications.js", "/js/views/initTran.html");
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
    	log.info("------- INICIO FILTRO PAYMENT -------");
    	log.info("******* Inicio de Filtragem de acesso aos serviços assíncronos *********");

		
		// TODO Auto-generated method stub
		if (!(request instanceof HttpServletRequest)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		HttpServletResponse res = (HttpServletResponse) response;       
        res.setHeader("Access-Control-Allow-Origin", "*"); 
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Access-Control-Allow-Headers, Content-Range, Content-Disposition, Content-Type, Accept, X-Header-Application, X-Header-Path, X-Auth-Token, X-Auth-Service, Authorization");

        if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
			res.setStatus(HttpServletResponse.SC_OK);
			return;
        }
		
		if(!ALLOWEDS.contains(httpRequest.getServletPath())) {

			log.info("Path = "+httpRequest.getServletPath());		
			
			String authToken = httpRequest.getHeader(Util.HEADER_SERVICE_KEY); 

			log.info("authToken = "+authToken);
			
			if(!Util.isNotNull(authToken) || !KEY.equals(authToken)) {
				throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
			}
		}
		
		log.info("******* Fim de Filtragem de acesso ao payment para = "+httpRequest.getRequestURI()+"*********");
    	log.info("------- FIM FILTRO PAYMENT -------");

		chain.doFilter(request, response);
	}
}
