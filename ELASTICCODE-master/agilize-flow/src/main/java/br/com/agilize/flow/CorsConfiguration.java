package br.com.agilize.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
@EnableWebMvc
public class CorsConfiguration extends WebMvcConfigurerAdapter {
	
	@Autowired
	private AppPropertiesService app;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		String urlEnabled = app.getPropertyString("url.enabled");
		
		registry.addMapping("/**")
			.allowedOrigins(urlEnabled.split(","))
			.allowedMethods("GET", "PUT", "POST", "DELETE", "HEAD", "OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true);//.maxAge(3600);
	}
	
	@Bean(name = "appPropertiesService")
	public AppPropertiesService getAppPropertiesService() {
		return new AppPropertiesService();
	}

}
