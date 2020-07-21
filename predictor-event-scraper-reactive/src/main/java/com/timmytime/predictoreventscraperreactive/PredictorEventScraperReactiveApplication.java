package com.timmytime.predictoreventscraperreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class PredictorEventScraperReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictorEventScraperReactiveApplication.class, args);
	}

}
