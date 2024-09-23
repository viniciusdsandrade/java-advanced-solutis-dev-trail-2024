package br.com.agilizeware.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

import br.com.agilizeware.security.component.AuthenticationFilter;
import br.com.agilizeware.security.component.CustomAuthenticationProvider;

@Configuration
//@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	public static final String TOKEN = "X-Auth-Token";

	@Autowired
	private CustomAuthenticationProvider provider;
	@Autowired
	private AuthenticationFilter authenticationFilter;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	        web
	        	//Spring Security ignores request to static resources such as CSS or JS files.
	            .ignoring().antMatchers("/static/**");
	}
	 
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                
                //TODO REMOVER LINHA ABAIXO
    			//.antMatchers("/agilize/facade").permitAll()
    			//TODO REMOVER LINHA ABAIXO
    			//.antMatchers("/agilize/isobrou/store").permitAll()
                
        		//Permitidos
        		.antMatchers("/agilize/login/authenticate", "/agilize/facade/enums", "/agilize/login/validateToken", "/agilize/facade/application").permitAll()
                .antMatchers("/error", "/failure", "/favicon.ico", "/auth/**", 
                		     "/signup/**", "/signin/**", "/api/session", "/js/**").permitAll()
                
                .antMatchers("/api/**", "/agilize/**").authenticated()
                
                .and()
                .headers().frameOptions().disable() // for h2
                .and()
                .requestCache()
                .requestCache(new NullRequestCache())
                .and()
    			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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
    			.authenticationProvider(provider)
    			.addFilterBefore(authenticationFilter, AnonymousAuthenticationFilter.class);
        
        /*http
        .authorizeRequests()
        .antMatchers("/api/session", "/agilize/**").permitAll()
        .antMatchers("/h2-console/**").permitAll()
        .antMatchers("/api/**").authenticated()
        .and()
        .headers().frameOptions().disable() // for h2
        .and()
        .requestCache()
        .requestCache(new NullRequestCache())
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .and().csrf().disable();*/
    }
}