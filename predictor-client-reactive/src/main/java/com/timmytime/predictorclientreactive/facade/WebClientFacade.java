package com.timmytime.predictorclientreactive.facade;

import com.timmytime.predictorclientreactive.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class WebClientFacade {

    public List<Team> getTeams(String url){
        return null;
    }

    public Flux<Event> getUpcomingEvents(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Event.class);
    }

    public Flux<EventOutcome> getPreviousEventOutcomes(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventOutcome.class);
    }

    public Flux<EventOutcome> getUpcomingEventOutcomes(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventOutcome.class);
    }


    public void startScraper(String url){

    }

    public Mono<Match> getMatch(String url){
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Match.class);

    }

    public Flux<Player> getPlayers(String url){
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Player.class);
    }

    public Mono<PlayerResponse> getPlayer(String url){
        return null;
    }

}