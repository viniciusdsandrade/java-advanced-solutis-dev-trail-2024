package br.com.agilizeware.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
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