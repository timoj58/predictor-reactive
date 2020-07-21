package com.timmytime.predictoreventsreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.request.Prediction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientFacade {

    public void sendMessage(String url, JsonNode payload){
        WebClient.builder().build()
                .post()
                .uri(url)
                .body(Mono.just(payload), JsonNode.class)
                .exchange()
                .subscribe();
    }

    public void predict(String url, Prediction prediction){
        WebClient.builder().build()
                .post()
                .uri(url)
                .body(Mono.just(
                        new ObjectMapper()
                                .convertValue(prediction, JsonNode.class)
                ), JsonNode.class)
                .exchange()
                .subscribe();

    }
}
