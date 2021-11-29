package com.timmytime.predictormessagereactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
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

    public Mono<ServerResponse> predictTeamResult(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.predictTeamResult(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("country"),
                request.bodyToMono(JsonNode.class)
        ));
    }

    public Mono<ServerResponse> predictTeamGoals(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.predictTeamGoals(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("country"),
                request.bodyToMono(JsonNode.class)
        ));
    }


    public Mono<ServerResponse> trainPlayers(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.trainPlayers(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("to"),
                request.pathVariable("from")
        ));
    }

    public Mono<ServerResponse> playerConfig(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.playerConfig(
                request.pathVariable("type")
        ));
    }

    public Mono<ServerResponse> predictPlayer(ServerRequest request) {
        return ServerResponse.ok().build(trainingTestService.predictPlayer(
                UUID.fromString(request.pathVariable("receipt")),
                Boolean.valueOf(request.pathVariable("init")),
                request.bodyToMono(JsonNode.class)
        ));
    }

}
