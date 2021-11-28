package com.timmytime.predictormessagereactive.handler;

import com.timmytime.predictormessagereactive.service.TrainingTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestHandler {

    private final TrainingTestService trainingTestService;

    public Mono<ServerResponse> trainTeams(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.trainTeams(
                UUID.fromString(request.pathVariable("receipt")),
                        request.pathVariable("to"),
                        request.pathVariable("from"),
                        request.pathVariable("country")
        ));
    }

    public Mono<ServerResponse> trainPlayers(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.trainPlayers(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("to"),
                request.pathVariable("from")
        ));
    }
}
