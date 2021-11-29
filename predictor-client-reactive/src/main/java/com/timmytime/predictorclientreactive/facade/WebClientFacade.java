package com.timmytime.predictorclientreactive.facade;

import com.timmytime.predictorclientreactive.model.*;
import com.timmytime.predictorclientreactive.request.FileRequest;
import com.timmytime.predictorclientreactive.request.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebClientFacade {

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

    public Flux<EventOutcome> getPreviousEventOutcomesByTeam(
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


    public Mono<Match> getMatch(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Match.class);

    }

    public Flux<Player> getPlayers(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Player.class);
    }

    public Mono<PlayerResponse> getPlayer(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(PlayerResponse.class);
    }

    public Flux<Player> getFantasyPlayers(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Player.class);
    }

    public Flux<EventOutcome> topMatchSelections(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventOutcome.class);

    }

    public Flux<FantasyOutcome> topPlayerSelections(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(FantasyOutcome.class);

    }

    public void sendMessage(String url, Message payload) {
        WebClient.builder().build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(payload), Message.class)
                .exchange()
                .subscribe();
    }

    public void put(String url, FileRequest fileRequest){
        WebClient.builder().build()
                .put()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(fileRequest), FileRequest.class)
                .exchange()
                .subscribe();

    }

}
