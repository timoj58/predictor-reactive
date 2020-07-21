package com.timmytime.predictorplayersreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
public class PredictorPlayersReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictorPlayersReactiveApplication.class, args);
	}

}
