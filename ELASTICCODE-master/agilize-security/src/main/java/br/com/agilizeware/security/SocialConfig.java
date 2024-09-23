package br.com.agilizeware.security;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import br.com.agilizeware.model.User;
import br.com.agilizeware.security.component.AuthUtil;
import br.com.agilizeware.util.AppPropertiesService;
import br.com.agilizeware.util.Util;

@Configuration
@EnableSocial
@PropertySource("classpath:application.properties")
public class SocialConfig implements SocialConfigurer {

    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SocialConfig.class);
    
    @Autowired
    private DataSource dataSource;
    @Autowired
	private AppPropertiesService appPropertiesService;
    @Autowired
    private AuthUtil authUtil;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfc, Environment env) {
        cfc.addConnectionFactory(getGoogleConnectionFactory());
    }
	
	@Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator cfl) {
        return getConnectionRepository(cfl);
    }
    
    private UsersConnectionRepository getConnectionRepository(ConnectionFactoryLocator cfl) {
    	if(cfl == null) {
    		cfl = connectionFactoryLocator();
    	}
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, cfl, Encryptors.noOpText());
        //repository.setConnectionSignUp(accountConnectionSignUpService);
        return repository;
    }

    @Bean
    public SocialConfigurer socialConfigurerAdapter(DataSource dataSource) {
        // https://github.com/spring-projects/spring-social/blob/master/spring-social-config/src/main/java/org/springframework/social/config/annotation/SocialConfiguration.java#L87
        return new DatabaseSocialConfigurer(dataSource);
    }

    @Bean
    public SignInAdapter authSignInAdapter() {
        return (userId, connection, request) -> {
        	User us = authUtil.authenticProfile(connection, Util.getTypeDeviceAtUserAgent(request.getHeader("user-agent")));
            return "/#/signin/logged/"+us.getToken();
        };
    }
    
    @Bean
    @Scope(value="singleton", proxyMode=ScopedProxyMode.INTERFACES)
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(getGoogleConnectionFactory());
        return registry;
    }
    
    @Bean 
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES) 
    public Google google(ConnectionRepository repository) { 
        Connection<Google> connection = repository.findPrimaryConnection(Google.class); 
        return connection != null ? connection.getApi() : new GoogleTemplate(); 
    } 
    
    private GoogleConnectionFactory getGoogleConnectionFactory() {
    	
    	GoogleConnectionFactory gcf = new GoogleConnectionFactory(
    			appPropertiesService.getPropertyString("spring.social.google.appId"),
    			appPropertiesService.getPropertyString("spring.social.google.appSecret"));
        gcf.setScope("https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email");
        return gcf;
    }
    
    @Bean
    public AuthUtil authUtil() {
    	return new AuthUtil();
    }
	
}