package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventOutcomeService {
    Mono<EventOutcome> save(EventOutcome eventOutcome);
    Mono<EventOutcome> find(UUID id);
    Flux<EventOutcome> toValidate(String country);
    Flux<EventOutcome> lastEvents(String country);
}
