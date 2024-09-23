package br.com.agilizeware.shopping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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

	public static final Logger log = LogManager.getLogger(ServiceAuthenticationFilter.class); 
	public static final String KEY = "kfisOKD98mdlk_)0=+0923mdjMiMuMt455lki*inshg##mxnajKKituscU79*0CoIo";
	public static final String NAME_APP = ApplicationNamesEnum.SHOPPING.getName();
	
	private static List<String> ALLOWEDS = Arrays.asList(
			"/", "/index.html", "/css/style.css", "/css/bootstrap-combined.min.css", "/js/lib/angular/angular.js", 
			"/js/lib/angular/angular-route-1.4.8.min.js", "/js/lib/angular/angular-locale_pt-br.js", 
			"/js/lib/jquery/jquery.js", "/js/lib/services/notifications.js", "/js/lib/common.js", "/js/lib/directive/pagination.js",
			"/js/lib/services/notifications.js", "/js/lib/services/localizedMessages.js", "/js/lib/services/localStorage.js", 
			"/js/lib/resources/rest-resource.js", "/js/lib/resources/angular-msgbox.js", "/js/lib/resources/devicecheck.js", 
			"/js/controller/store.js", "/js/lib/services/i18nNotifications.js", "/js/appmsg.js", "/css/bootstrap.css.map", 
			"/js/lib/angular/angular-route.min.js.map", "/js/lib/services/i18nNotifications.js", "/js/app.js", "/templates/store.html",
			"/templates/img/logo.png", "/js/lib/css/ui-bootstrap-tpls.js", "/img/glyphicons-halflings.png", "/js/service/shoppingCart.js",
			"/js/controller/product.js", "/templates/product.html", "/img/glyphicons-halflings-white.png", "/js/controller/payment.js",
			"/templates/shoppingCart.html", "/js/lib/angular/font_awesome_fb016fbddf.js", "/css/bootstrap.css",
			"/templates/img/background-home4.jpg");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		log.info("*** INICIO FILTER SHOPPING *****");
		
		// TODO Auto-generated method stub
		if (!(request instanceof HttpServletRequest)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		log.info("Path = "+httpRequest.getServletPath());
		
		String authToken = httpRequest.getHeader(Util.HEADER_SERVICE_KEY); 
		if(!ALLOWEDS.contains(httpRequest.getServletPath()) && (!Util.isNotNull(authToken) || !KEY.equals(authToken))) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
		}

		chain.doFilter(request, response);
		log.info("--- FIM FILTER SHOPPING ---");
	}
}
