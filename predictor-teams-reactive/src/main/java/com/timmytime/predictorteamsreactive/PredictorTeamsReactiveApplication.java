package com.timmytime.predictorteamsreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableMongoRepositories
public class PredictorTeamsReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictorTeamsReactiveApplication.class, args);
	}

}
