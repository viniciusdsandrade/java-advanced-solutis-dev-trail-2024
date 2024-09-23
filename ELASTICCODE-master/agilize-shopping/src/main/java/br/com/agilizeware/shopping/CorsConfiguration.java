package br.com.agilizeware.shopping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
@EnableTransactionManagement
public class CorsConfiguration extends WebMvcConfigurerAdapter {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String urlEnabled = getAppPropertiesService().getPropertyString("url.enabled");
		registry.addMapping("/**").allowedOrigins(urlEnabled.split(","))
			.allowedMethods("*")
			.allowCredentials(true);
	}
	
	@Bean(name = "appPropertiesService")
	public AppPropertiesService getAppPropertiesService() {
		return new AppPropertiesService();
	}
}