package br.com.agilizeware.asynchronous.component;

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

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAuthenticationFilter extends GenericFilterBean {
	
	private static final Logger log = LogManager.getLogger(ServiceAuthenticationFilter.class);
	public static final String KEY = "KSI98kmuybIWn8102N9n9N9UIS910J23hj8g0a(SLiux)JNhh721aAbbbuUU19432l0lI1I1";
	
    private static final List<String> ALLOW_ACCESS = Arrays.asList(
        "/",
        "/index.html"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	log.info("------- INICIO FILTRO ASSYNC SERVICE -------");
    	log.info("******* Inicio de Filtragem de acesso aos serviços assíncronos *********");
        
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
        
        String accessKey = httpRequest.getHeader(Util.HEADER_SERVICE_KEY); 
		if(!Util.isNotNull(accessKey)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD);
		}
        
        if (httpRequest.getMethod().equals("POST")) {
        	if(ServiceAuthenticationFilter.ALLOW_ACCESS.contains(httpRequest.getServletPath())) {
        		chain.doFilter(request, response);
        	}
        	else {
        		if(!accessKey.equals(KEY)) {
        			log.info("Acesso Não Autorizado para = "+httpRequest.getRequestURI()+". Senha inválida.");
        			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
        		}
        	}
        }
        else {
			log.info("Acesso Não Autorizado para = "+httpRequest.getRequestURI()+". Método Http inválido.");
			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
        }
        
    	log.info("******* Fim de Filtragem de acesso aos serviços assíncronos com sucesso para = "+httpRequest.getRequestURI()+"*********");
    	log.info("------- FIM FILTRO ASSYNC SERVICE -------");
        chain.doFilter(request, response);
    }

}
