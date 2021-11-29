package com.timmytime.predictorplayerseventsreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorplayerseventsreactive.model.*;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import com.timmytime.predictorplayerseventsreactive.request.PlayerEventOutcomeCsv;
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


    public void sendMessage(String url, Message payload) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(payload), Message.class)
                .exchange()
                .subscribe();
    }

    public Flux<Player> getPlayers(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Player.class);

    }

    public Mono<Player> getPlayer(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Player.class);

    }

    public Flux<LineupPlayer> getAppearances(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(LineupPlayer.class);

    }

    public Mono<Match> getMatch(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Match.class);

    }

    public Flux<StatMetric> getStats(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(StatMetric.class);

    }

    public Flux<Event> getEvents(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Event.class);
    }

    public void predict(String url, PlayerEventOutcomeCsv playerEventOutcomeCsv) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .body(Mono.just(
                        new ObjectMapper().convertValue(
                                playerEventOutcomeCsv, JsonNode.class)
                ), JsonNode.class)
                .exchange()
                .subscribe();
    }

    public void config(String url) {
        log.info("url {}", url);
        WebClient.builder().build()
                .put()
                .uri(url)
                .exchange()
                .subscribe();
    }

}
