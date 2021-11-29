package com.timmytime.predictorplayerseventsreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorplayerseventsreactive.model.LineupPlayer;
import com.timmytime.predictorplayerseventsreactive.model.Match;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.model.StatMetric;
import com.timmytime.predictorplayerseventsreactive.request.Message;
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


    public void train(String url) {
        log.info("url post {}", url);
        WebClient.builder().build()
                .post()
                .uri(url)
                .exchange()
                .subscribe();
    }

}
