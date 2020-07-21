package com.timmytime.predictorteamsreactive.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class WebClientFacade {

    public Flux<Match> getMatches(String url){
        return WebClient.builder().build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Match.class);

    }

    public void sendMessage(String url, JsonNode payload){
         WebClient.builder().build()
                .post()
                .uri(url)
                .body(Mono.just(payload), JsonNode.class)
                .exchange()
                .subscribe();
    }

    public void train(String url){
              WebClient.builder().build()
                 .post()
                 .uri(url)
                 .exchange()
                 .subscribe();
    }
}
