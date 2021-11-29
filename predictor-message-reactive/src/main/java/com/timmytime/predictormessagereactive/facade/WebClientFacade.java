package com.timmytime.predictormessagereactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictormessagereactive.request.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebClientFacade {

    public void scrape(String url) {
        log.info("scraping {}", url);
        WebClient.builder().build()
                .post()
                .uri(url)
                .exchange()
                .subscribe();
    }

    public void train(String url, Message message){
        log.info("train {}", url);
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(message), Message.class)
                .exchangeToMono(Mono::just)
                .subscribe();

    }

    public void predict(String url, Message message){
        log.info("predict {}", url);
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(message), Message.class)
                .exchangeToMono(Mono::just)
                .subscribe();

    }

    public void finish(String url, Message message){
        log.info("finish {}", url);
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(message), Message.class)
                .exchangeToMono(Mono::just)
                .subscribe();

    }

    public void init(String url){
        log.info("init {}", url);
        WebClient.builder().build()
                .post()
                .uri(url)
                .exchange()
                .subscribe();
    }

    public void receipt(String url){
        log.info("receipt {}", url);
        WebClient.builder().build()
                .put()
                .uri(url)
                .exchange()
                .subscribe();

    }

    public void receipt(String url, JsonNode prediction){
        log.info("receipt {}", url);
        WebClient.builder().build()
                .put()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(prediction), JsonNode.class)
                .exchange()
                .subscribe();

    }


}
