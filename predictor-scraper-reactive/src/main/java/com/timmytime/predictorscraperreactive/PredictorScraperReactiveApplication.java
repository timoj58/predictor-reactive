package com.timmytime.predictorscraperreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableScheduling
@SpringBootApplication
@EnableWebFlux
public class PredictorScraperReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictorScraperReactiveApplication.class, args);
    }

}
