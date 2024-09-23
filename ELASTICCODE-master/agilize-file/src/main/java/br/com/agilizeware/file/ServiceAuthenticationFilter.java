package br.com.agilizeware.file;

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
	private static final String KEY = "12wsmvkmLD03Llkdmco84dm9k120Io01Okmsjhk84fks9mv799skc8jGG09282J8J6G0kd";
	public static final String NAME_APP = ApplicationNamesEnum.FILESERVER.getName();

	private static final List<String> PATHS_FREE = Arrays.asList("/file/upload/",
			"/file/fileById/");
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
    	log.info("------- INICIO FILTRO FILE -------");
    	log.info("******* Inicio de Filtragem de acesso ao File Server *********");
		
		// TODO Auto-generated method stub
		if (!(request instanceof HttpServletRequest)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		log.info("Request Path = "+httpRequest.getServletPath());
		
		HttpServletResponse res = (HttpServletResponse) response;       
        res.setHeader("Access-Control-Allow-Origin", "*"); 
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Access-Control-Allow-Headers, Content-Range, Content-Disposition, Content-Type, Accept, X-Header-Application, X-Header-Path, X-Auth-Token, X-Auth-Service, Authorization");

        if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
			res.setStatus(HttpServletResponse.SC_OK);
			return;
        }

		boolean authorized = false;
		for(String str : PATHS_FREE) {
			if(httpRequest.getServletPath().startsWith(str)) {
				authorized = true;
				break;
			}
		}
		
		String authToken = httpRequest.getHeader(Util.HEADER_SERVICE_KEY); 
		
		if((!authorized && (!Util.isNotNull(authToken) || !KEY.equals(authToken))) && !"/".equals(httpRequest.getServletPath())) {
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
		}

    	log.info("------- FIM FILTRO FILE -------");
		chain.doFilter(request, response);
	}
}
