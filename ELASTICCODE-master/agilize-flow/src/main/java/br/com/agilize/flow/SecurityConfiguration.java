package br.com.agilize.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import br.com.agilize.flow.component.ServiceAuthenticationFilter;
import br.com.agilizeware.util.AppPropertiesService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServiceAuthenticationFilter filter;
    @Autowired
	private AppPropertiesService appPropertiesService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.antMatchers(appPropertiesService.getPropertyString("server.contextPath")+"/**").permitAll()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().anonymous()
            .and().securityContext()
            .and().headers().disable()
            .rememberMe().disable()
            .requestCache().disable()
            .x509().disable()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .addFilterBefore(this.filter, AnonymousAuthenticationFilter.class);
    }
}
