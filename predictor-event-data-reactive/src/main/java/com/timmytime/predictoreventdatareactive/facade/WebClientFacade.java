package com.timmytime.predictoreventdatareactive.facade;

import com.timmytime.predictoreventdatareactive.model.MatchTeams;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientFacade {

    public Mono<MatchTeams> getMatchTeams(String url) {
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(MatchTeams.class);
    }
}
