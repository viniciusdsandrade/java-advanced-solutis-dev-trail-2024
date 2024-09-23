package br.com.agilizeware.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
//@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ServiceAuthenticationFilter filter;
	@Autowired
	private AppPropertiesService appPropertiesService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// defines the authentication for application entrypoints
			.authorizeRequests()
			
			.antMatchers(appPropertiesService.getPropertyString("server.contextPath")+"/**").permitAll()
			//.antMatchers("/agilize/**").permitAll()
			
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
			.addFilterBefore(filter, AnonymousAuthenticationFilter.class);
	}
}