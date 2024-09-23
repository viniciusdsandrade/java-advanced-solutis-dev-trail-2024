package br.com.agilizeware.security;

import java.util.Properties;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;

import br.com.agilizeware.rest.AppErrorRest;
import br.com.agilizeware.security.component.AuthenticationFilter;
import br.com.agilizeware.security.component.AuthorizationInterceptor;
import br.com.agilizeware.util.AppPropertiesService;

@Configuration
@ComponentScan(basePackages = { "br.com.agilizeware.security.repository.*" })
@EnableJpaRepositories(basePackages = { "br.com.agilizeware.security.repository" })
@EnableTransactionManagement
public class CorsConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private AppPropertiesService appPropertiesService;
	@Autowired
	private AuthorizationInterceptor authorizationInterceptor;
	@Autowired
	private ErrorAttributes errorAttributes;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String urlEnabled = appPropertiesService.getPropertyString("url.enabled");
		registry.addMapping("/**").allowedOrigins(urlEnabled.split(","))
				.allowedMethods("*")
				.allowedHeaders("*")
		        .exposedHeaders("X-Header-Application", "X-Header-Path", "X-Auth-Token", "Access-Control-Allow-Origin")
				.allowCredentials(true);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getInterceptor());
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(appPropertiesService.getPropertyString("spring.datasource.driverClassName"));
		dataSource.setUrl(appPropertiesService.getPropertyString("spring.datasource.url"));
		dataSource.setUsername(appPropertiesService.getPropertyString("spring.datasource.username"));
		dataSource.setPassword(appPropertiesService.getPropertyString("spring.datasource.password"));
		return dataSource;
	}

	private Properties getHibernateProperties() {
		Properties prop = new Properties();
		prop.put("hibernate.format_sql", "false");
		prop.put("hibernate.show_sql", "true");
		//prop.put("hibernate.hbm2ddl.auto", "update");
		prop.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		return prop;
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
		lcemfb.setPackagesToScan(appPropertiesService.getPropertyString("entitymanager.packagesToScan"));
		lcemfb.setDataSource(dataSource());
		lcemfb.setJpaProperties(getHibernateProperties());
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		lcemfb.setJpaVendorAdapter(jpaVendorAdapter);
		return lcemfb;
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager jtm = new JpaTransactionManager();
		jtm.setEntityManagerFactory(getLocalContainerEntityManagerFactoryBean().getObject());
		jtm.setDataSource(dataSource());
		return jtm;
	}

	@Bean(name = "authenticationFilter")
	public Filter authenticationFilter() {
		return new AuthenticationFilter();
	}
	
	@Bean(name = "appPropertiesService")
	public AppPropertiesService getAppPropertiesService() {
		return new AppPropertiesService();
	}
	
	@Bean
	public MappedInterceptor getInterceptor() {
		return new MappedInterceptor(new String[] { "/agilize/**" }, authorizationInterceptor);
	}
	
	@Bean
	public AppErrorRest appErrorController() {
		return new AppErrorRest(errorAttributes);
	}

	/**
	 * PersistenceExceptionTranslationPostProcessor is a bean post processor
	 * which adds an advisor to any bean annotated with Repository so that any
	 * platform-specific exceptions are caught and then rethrown as one Spring's
	 * unchecked data access exceptions (i.e. a subclass of
	 * DataAccessException).
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
}