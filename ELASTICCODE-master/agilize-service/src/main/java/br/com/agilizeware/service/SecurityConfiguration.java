package br.com.agilizeware.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
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
			// POST to /rest/oauth/login is not authenticated
			// GET /rest/oauth/token is not authenticated
			//.antMatchers(HttpMethod.GET, "/rest/oauth/token").permitAll()
			// the other REST APIs are authenticated
			.antMatchers(appPropertiesService.getPropertyString("server.contextPath")+"/**").permitAll()
			//.antMatchers("/agilize/**").permitAll()
			.and()
			// never use server side sessions (stateless mode)
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
			//.authenticationProvider(provider)
			// add custom authentication filter
			.addFilterBefore(filter, AnonymousAuthenticationFilter.class);
			// register custom authentication exception handler
			//.exceptionHandling().authenticationEntryPoint(entryPoint);
	}
}