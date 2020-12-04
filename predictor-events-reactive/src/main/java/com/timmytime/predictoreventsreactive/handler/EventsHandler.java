package com.timmytime.predictoreventsreactive.handler;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Component
public class EventsHandler {

    private final EventOutcomeService eventOutcomeService;

    @Autowired
    public EventsHandler(
            EventOutcomeService eventOutcomeService
    ) {
        this.eventOutcomeService = eventOutcomeService;
    }

    public Mono<ServerResponse> currentEvents(ServerRequest request) {

        return ServerResponse.ok().body(
                eventOutcomeService.currentEvents(
                        request.pathVariable("competition")),
                EventOutcome.class
        );
    }

    public Mono<ServerResponse> previousEvents(ServerRequest request) {

        return ServerResponse.ok().body(
                eventOutcomeService.previousEvents(
                        request.pathVariable("competition")),
                EventOutcome.class
        );
    }

    public Mono<ServerResponse> previousEventsByTeam(ServerRequest request) {

        return ServerResponse.ok().body(
                eventOutcomeService.previousEventsByTeam(
                        UUID.fromString(request.pathVariable("team"))),
                EventOutcome.class
        );
    }
}
