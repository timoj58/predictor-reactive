package com.timmytime.predictorteamsreactive.facade;

import com.timmytime.predictorteamsreactive.model.EventOutcome;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class WebClientFacade {

    public Flux<Match> getMatches(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Match.class);

    }

    public Mono<Match> getMatch(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Match.class);

    }

    public Flux<EventOutcome> getOutstandingEvents(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventOutcome.class);

    }

    public void sendMessage(String url, Message payload) {
        log.info("sending {}", payload.toString());

        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(payload), Message.class)
                .exchange()
                .subscribe();
    }

    public void train(String url) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .exchange()
                .subscribe();
    }

}
