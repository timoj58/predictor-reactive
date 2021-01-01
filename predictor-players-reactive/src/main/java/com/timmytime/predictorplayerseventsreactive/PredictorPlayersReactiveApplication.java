package com.timmytime.predictorplayerseventsreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableMongoRepositories
@EnableScheduling
public class PredictorPlayersReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictorPlayersReactiveApplication.class, args);
    }

}
