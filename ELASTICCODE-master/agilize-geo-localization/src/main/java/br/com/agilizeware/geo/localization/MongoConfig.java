package br.com.agilizeware.geo.localization;

import br.com.agilizeware.util.AppPropertiesService;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Autowired
    private AppPropertiesService appPropertiesService;

    @Override
    public String getDatabaseName() {
        return this.appPropertiesService.getPropertyString("mongodb.database");
    }

    @Override
    @Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient(this.appPropertiesService.getPropertyString("mongodb.server"));
    }
}
