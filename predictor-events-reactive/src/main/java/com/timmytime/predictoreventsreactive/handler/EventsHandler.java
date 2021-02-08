package com.timmytime.predictoreventsreactive.handler;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class EventsHandler {

    private final EventOutcomeService eventOutcomeService;

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

    public Mono<ServerResponse> topSelections(ServerRequest request) {

        return ServerResponse.ok().body(
                eventOutcomeService.topSelections(
                        request.pathVariable("outcome"),
                        Integer.valueOf(request.queryParam("threshold").get())),
                EventOutcome.class
        );
    }
}
