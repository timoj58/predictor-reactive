package com.timmytime.predictoreventsreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
@EnableScheduling
public class PredictorEventsReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictorEventsReactiveApplication.class, args);
    }

}
