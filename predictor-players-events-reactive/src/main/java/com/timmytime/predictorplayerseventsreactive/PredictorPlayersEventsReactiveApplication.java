package com.timmytime.predictorplayerseventsreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
@EnableScheduling
public class PredictorPlayersEventsReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictorPlayersEventsReactiveApplication.class, args);
    }

}
