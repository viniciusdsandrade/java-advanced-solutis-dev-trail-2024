package br.com.agilizeware.shopping;

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
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AppPropertiesService appPropertiesService;
	@Autowired
	private ServiceAuthenticationFilter filter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// defines the authentication for application entrypoints
			.authorizeRequests()
			
			.antMatchers(appPropertiesService.getPropertyString("server.contextPath")+"/**").permitAll()
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