package com.timmytime.predictorclientreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class PredictorClientReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictorClientReactiveApplication.class, args);
	}

}
