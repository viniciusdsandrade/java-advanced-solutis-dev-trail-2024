package br.com.agilizeware.geo.localization;

import br.com.agilizeware.util.AppPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@org.springframework.web.servlet.config.annotation.EnableWebMvc
public class CorsConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private AppPropertiesService appPropertiesService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(this.appPropertiesService.getPropertyString("url.enabled").split(",")).allowedMethods("*").allowCredentials(true);
    }

    @Bean(name = "appPropertiesService")
    public AppPropertiesService getAppPropertiesService() {
        return new AppPropertiesService();
    }
}
