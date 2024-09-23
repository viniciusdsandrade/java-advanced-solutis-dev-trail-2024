package br.com.agilizeware.geo.localization;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;
import br.com.agilizeware.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAuthenticationFilter extends GenericFilterBean {

    private static final String KEY = "23dKLiopXi89J6jhy2snY7Y7J9kas00mnI1l42ewTBVFIo091aLOo01o";
    private static final List<String> ALLOW_ACCESS = Arrays.asList(
        "/",
        "/index.html",
        "/home/content.html",
        "/home/scripts/default.js",
        "/local/content.html",
        "/local/list.html",
        "/local/scripts/default.js",
        "/scripts/angular-1.6.2.min.js",
        "/scripts/angular-1.6.2.min.js.map",
        "/scripts/angular-route-1.6.2.min.js",
        "/scripts/angular-route-1.6.2.min.js.map",
        "/scripts/bootstrap-3.3.7.min.js",
        "/scripts/default.js",
        "/scripts/jquery-3.1.1.min.js",
        "/styles/bootstrap-3.3.7.min.css",
        "/styles/bootstrap-3.3.7.min.css.map",
        "/styles/default.css",
        "/images/favicon.png",
        "/images/loading.gif"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new AgilizeException(HttpStatus.BAD_REQUEST.ordinal(), ErrorCodeEnum.EXPECTED_HTTP_REQUEST);
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (!httpRequest.getMethod().equals("GET") || !ServiceAuthenticationFilter.ALLOW_ACCESS.contains(httpRequest.getServletPath())) {
            String authToken = httpRequest.getHeader(Util.HEADER_SERVICE_KEY);
            if (!Util.isNotNull(authToken) || !ServiceAuthenticationFilter.KEY.equals(authToken)) {
                System.out.println(httpRequest.getMethod() + ": " + httpRequest.getServletPath());
                throw new AgilizeException(HttpStatus.UNAUTHORIZED.ordinal(), ErrorCodeEnum.UNAUTHORIZED_ACCESS);
            }
        }
        chain.doFilter(request, response);
    }
}
