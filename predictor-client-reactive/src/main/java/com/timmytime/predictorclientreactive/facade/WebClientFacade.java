package com.timmytime.predictorclientreactive.facade;

import com.timmytime.predictorclientreactive.model.Event;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.Match;
import com.timmytime.predictorclientreactive.model.Team;
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

    public Flux<Event> getEvents(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Event.class);
    }

    public Flux<EventOutcome> getPreviousEvents(
            String url
    ) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventOutcome.class);
    }

    public Flux<EventOutcome> getUpcomingEvents(
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

}
