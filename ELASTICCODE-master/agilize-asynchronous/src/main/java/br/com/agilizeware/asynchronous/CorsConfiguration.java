package br.com.agilizeware.asynchronous;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.agilizeware.rest.ServiceRestAb;
import br.com.agilizeware.util.AppPropertiesService;

@Configuration
public class CorsConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private AppPropertiesService appPropertiesService;
    
    private static final Logger log = LogManager.getLogger(ServiceRestAb.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //registry.addMapping("/**").allowedOrigins(this.appPropertiesService.getPropertyString("url.enabled").split(",")).allowedMethods("*").allowCredentials(true);
    	registry.addMapping("/**").allowedOrigins("*")
			.allowedMethods("*")
			.allowedHeaders("*")
			.exposedHeaders("X-Header-Application", "X-Header-Path", "X-Auth-Token", "Access-Control-Allow-Origin", "X-Auth-Service")
			.allowCredentials(true);
    }

    @Bean(name = "appPropertiesService")
    public AppPropertiesService getAppPropertiesService() {
        return new AppPropertiesService();
    }
    
    @Bean(name = "dataSource")
	public DataSource dataSource() {
    	
    	log.info("Agilize-Assync | CorsConfiguration | BEAN | dataSource ");
    	log.info("spring.datasource.driverClassName = "+appPropertiesService.getPropertyString("spring.datasource.driverClassName"));
    	log.info("spring.datasource.url = "+appPropertiesService.getPropertyString("spring.datasource.url"));
    	log.info("spring.datasource.username = "+appPropertiesService.getPropertyString("spring.datasource.username"));
    	log.info("spring.datasource.password = "+appPropertiesService.getPropertyString("spring.datasource.password"));
    	
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
    
    @Bean
    public VelocityEngine velocityEngine() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("input.encoding", appPropertiesService.getPropertyString("spring.mail.default-encoding"));
        properties.setProperty("output.encoding", appPropertiesService.getPropertyString("spring.mail.default-encoding"));
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine velocityEngine = new VelocityEngine(properties);
        return velocityEngine;
    }
}
