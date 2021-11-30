package com.timmytime.predictormessagereactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictormessagereactive.model.FileRequest;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import com.timmytime.predictormessagereactive.service.TestApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestHandler {

    private final TestApiService testApiService;
    private final OrchestrationService orchestrationService;

    public Mono<ServerResponse> trainTeams(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.trainTeams(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("to"),
                request.pathVariable("from"),
                request.pathVariable("country")
        ));
    }

    public Mono<ServerResponse> predictTeamResult(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.predictTeamResult(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("country"),
                request.bodyToMono(JsonNode.class)
        ));
    }

    public Mono<ServerResponse> predictTeamGoals(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.predictTeamGoals(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("country"),
                request.bodyToMono(JsonNode.class)
        ));
    }


    public Mono<ServerResponse> trainPlayers(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.trainPlayers(
                UUID.fromString(request.pathVariable("receipt")),
                request.pathVariable("to"),
                request.pathVariable("from")
        ));
    }

    public Mono<ServerResponse> playerConfig(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.playerConfig(
                request.pathVariable("type")
        ));
    }

    public Mono<ServerResponse> predictPlayer(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.predictPlayer(
                UUID.fromString(request.pathVariable("receipt")),
                Boolean.valueOf(request.pathVariable("init")),
                request.bodyToMono(JsonNode.class)
        ));
    }

    public Mono<ServerResponse> uploadFile(ServerRequest request) {
        return ServerResponse.ok().build(testApiService.uploadFile(request.bodyToMono(FileRequest.class)));
    }

    public Mono<ServerResponse> testStatus(ServerRequest request) {
        return ServerResponse.ok()
                .body(orchestrationService.testStatus(request.pathVariable("action")),Boolean.class);
    }

}
