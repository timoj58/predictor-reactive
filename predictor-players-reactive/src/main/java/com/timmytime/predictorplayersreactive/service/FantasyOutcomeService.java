package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface FantasyOutcomeService {
    Mono<FantasyOutcome> save(FantasyOutcome fantasyOutcome);
    Mono<FantasyOutcome> find(UUID id);
    Flux<FantasyOutcome> findByPlayer(UUID id);

}
