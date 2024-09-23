package br.com.agilizeware.isobrou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
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
	
	@Bean(name = "appPropertiesService")
	public AppPropertiesService getAppPropertiesService() {
		return new AppPropertiesService();
	}
}