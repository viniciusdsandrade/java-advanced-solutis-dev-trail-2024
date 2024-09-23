package br.com.agilize.flow.component;

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

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAuthenticationFilter extends GenericFilterBean {
	
	private static final Logger log = LogManager.getLogger(ServiceAuthenticationFilter.class);
	public static final String KEY = "jfskfjku8J0Loi394$%UinbU$0o0LjkwIJLnbB7y989@1!kjsiKICUmeiL0KJU$dR%";
	
    private static final List<String> ALLOW_ACCESS = Arrays.asList(
        "/",
        "/index.html"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	log.info("------- INICIO FILTRO FLOW SERVICE -------");
    	log.info("******* Inicio de Filtragem de acesso ao workflow *********");
        
    	if (!(request instanceof HttpServletRequest)) {
            throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
        }
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
    	
        String accessKey = httpRequest.getHeader(Util.HEADER_SERVICE_KEY); 
		if(!Util.isNotNull(accessKey)) {
			throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.REQUIREDS_FIELD);
		}
        
    	if(ServiceAuthenticationFilter.ALLOW_ACCESS.contains(httpRequest.getServletPath())) {
    		chain.doFilter(request, response);
    	}
    	else {
    		if(!accessKey.equals(KEY)) {
    			log.info("Acesso Não Autorizado para = "+httpRequest.getRequestURI()+". Senha inválida.");
    			throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
    		}
    	}
        
    	log.info("******* Fim de Filtragem de acesso aos serviços de WorkFlow com sucesso para = "+httpRequest.getRequestURI()+"*********");
    	log.info("------- FIM FILTRO FLOW SERVICE -------");
        chain.doFilter(request, response);
    }

}
