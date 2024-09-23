package br.com.agilizeware.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import br.com.agilizeware.util.AppPropertiesService;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
	
	@Autowired
	private AppPropertiesService appPropertiesService;
	
	@Override
	public String getDatabaseName() {
		//return "service_agilize_mongodb";
		return appPropertiesService.getPropertyString("data.mongodb.database");
	}
	
	@Override
	@Bean
	public Mongo mongo() throws Exception {
		//return new MongoClient("127.0.0.1");
		return new MongoClient(new MongoClientURI(appPropertiesService.getPropertyString("data.mongodb.uri")));
	}

}
