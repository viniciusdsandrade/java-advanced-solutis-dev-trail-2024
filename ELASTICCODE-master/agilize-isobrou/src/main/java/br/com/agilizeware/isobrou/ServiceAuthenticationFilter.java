package br.com.agilizeware.isobrou;

import java.io.IOException;

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

	private static final Logger log = LogManager.getLogger(ServiceAuthenticationFilter.class); 
	public static final String KEY = "ws873UJ7jh*($tFyj&7798203293jshmnxwJ7H)0OlkjqamndesU81I3kMj9433K";
	public static final String NAME_APP = ApplicationNamesEnum.ISOBROU.getName();
	/*private static final List<String> ALLOW_ACCESS = Arrays.asList(
			"/customer", "/store"
	);*/

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		log.info("*** INICIO FILTER ISOBROU *****");
		
		// TODO Auto-generated method stub
		if (!(request instanceof HttpServletRequest)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		log.info("Path = "+httpRequest.getServletPath());
		
		/*HttpServletResponse res = (HttpServletResponse) response;       
        res.setHeader("Access-Control-Allow-Origin", "*"); 
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Access-Control-Allow-Headers, Content-Range, Content-Disposition, Content-Type, Accept, X-Header-Application, X-Header-Path, X-Auth-Token, Authorization");*/

        /*if(ALLOW_ACCESS.contains(httpRequest.getServletPath())) {
			res.setStatus(HttpServletResponse.SC_OK);
			log.info("Free Pass");
			log.info("--- FIM FILTER ISOBROU ---");
			return;	
        }*/
        
		String authToken = httpRequest.getHeader(Util.HEADER_SERVICE_KEY); 
		if(!Util.isNotNull(authToken) || !KEY.equals(authToken)) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
		}

		chain.doFilter(request, response);
		log.info("--- FIM FILTER ISOBROU ---");
	}
}
