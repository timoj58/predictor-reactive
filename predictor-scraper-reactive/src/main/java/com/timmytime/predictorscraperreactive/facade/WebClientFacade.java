package com.timmytime.predictorscraperreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorscraperreactive.request.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientFacade {

    public void send(String url) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .exchange()
                .subscribe();
    }

    public void send(String url, Message message) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(message), Message.class)
                .exchange()
                .subscribe();
    }

    public void send(String url, JsonNode message) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(message), JsonNode.class)
                .exchange()
                .subscribe();
    }
}
