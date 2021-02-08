package com.timmytime.predictorplayerseventsreactive.handler;

import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EventsHandler {

    private final FantasyOutcomeService fantasyOutcomeService;

    public Mono<ServerResponse> topSelections(ServerRequest request) {

        return ServerResponse.ok().body(
                fantasyOutcomeService.topSelections(
                        request.pathVariable("market"),
                        Integer.valueOf(request.queryParam("threshold").get())),
                FantasyOutcome.class
        );
    }
}
