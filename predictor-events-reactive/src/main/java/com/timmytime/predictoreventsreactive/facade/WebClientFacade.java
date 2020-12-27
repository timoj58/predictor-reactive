package com.timmytime.predictoreventsreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventsreactive.model.Match;
import com.timmytime.predictoreventsreactive.request.Prediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebClientFacade {

    public void sendMessage(String url, JsonNode payload) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(payload), JsonNode.class)
                .exchange()
                .subscribe();
    }

    public void predict(String url, Prediction prediction) {

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

    public Mono<Match> getMatch(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Match.class);

    }
}
