package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventOutcomeService {
    Mono<EventOutcome> save(EventOutcome eventOutcome);

    Mono<EventOutcome> find(UUID id);

    Flux<EventOutcome> toValidate(String country);

    Flux<EventOutcome> lastEvents(String country);

    Flux<EventOutcome> previousEvents(@PathVariable String competition);

    Flux<EventOutcome> currentEvents(@PathVariable String competition);

    Flux<EventOutcome> previousEventsByTeam(@PathVariable UUID team);

    Flux<EventOutcome> toFix();

    Flux<EventOutcome> outstandingEvents(@PathVariable String country);

    Flux<EventOutcome> topSelections(@PathVariable String outcome, @RequestParam Integer threshold);
}
