package br.com.agilizeware.service;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.agilizeware.util.AppPropertiesService;


@Configuration
@EnableWebMvc
@ComponentScan( basePackages = { "br.com.agilizeware.service.repository.*" })
@EnableJpaRepositories(basePackages = {"br.com.agilizeware.service.repository"})
@EnableTransactionManagement
public class CorsConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private AppPropertiesService appPropertiesService;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		String urlEnabled = appPropertiesService.getPropertyString("url.enabled");
		registry.addMapping("/**").allowedOrigins(urlEnabled.split(","))
			.allowedMethods("*")
			.allowCredentials(true);
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
	
	@Bean(name = "appPropertiesService")
	public AppPropertiesService getAppPropertiesService() {
		return new AppPropertiesService();
	}

}