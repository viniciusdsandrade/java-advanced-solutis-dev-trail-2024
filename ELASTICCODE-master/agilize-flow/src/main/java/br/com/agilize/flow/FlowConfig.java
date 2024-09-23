package br.com.agilize.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class FlowConfig {

	public static void main(String[] args) throws Exception {
    	//SpringApplication.run("classpath:/META-INF/application-context.xml", args);
    	SpringApplication.run(FlowConfig.class, args);
    }
}
