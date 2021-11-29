package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FantasyOutcomeService {
    Mono<FantasyOutcome> save(FantasyOutcome fantasyOutcome);

    Mono<FantasyOutcome> find(UUID id);

    Flux<FantasyOutcome> findByPlayer(UUID id);

    Flux<FantasyOutcome> toFix();

    Flux<FantasyOutcome> reset();

    Flux<FantasyOutcome> topSelections(@PathVariable String market, @RequestParam Integer threshold);

    void init();

}
