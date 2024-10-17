package com.mightyjava;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
@EnableAsync
public class Application {
    public static void main(String[] args) {
        run(Application.class, args);
    }
}

