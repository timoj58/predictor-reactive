package com.timmytime.predictormessagereactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
public class PredictorMessageReactiveApplication {
	public static void main(String[] args) {
		SpringApplication.run(PredictorMessageReactiveApplication.class, args);
	}
}
