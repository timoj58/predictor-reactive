package com.timmytime.predictorplayersreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorplayersreactive.model.*;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebClientFacade {

    public Flux<Player> getPlayers(
            String url
    ){
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Player.class);

    }

    public Flux<LineupPlayer> getAppearances(
            String url
    ){
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(LineupPlayer.class);

    }

    public Mono<Match> getMatch(
            String url
    ){
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Match.class);

    }

    public Flux<StatMetric> getStats(
            String url
    ){
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

    public void predict(String url, PlayerEventOutcomeCsv playerEventOutcomeCsv){
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

    public void config(String url){
        WebClient.builder().build()
                .put()
                .uri(url)
                .exchange()
                .subscribe();
    }
}
