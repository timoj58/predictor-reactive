package com.timmytime.predictordatareactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictordatareactive.service.MessageReceivedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class MessageHandler {

    private final MessageReceivedService messageReceivedService;

    public Mono<ServerResponse> receive(ServerRequest request) {

        Mono<JsonNode> message = request.bodyToMono(JsonNode.class);

        return ServerResponse.ok().build(
                messageReceivedService.receive(message)
        );
    }

    public Mono<ServerResponse> completed(ServerRequest request) {

        return ServerResponse.ok().build(
                messageReceivedService.completed()
        );
    }

    public Mono<ServerResponse> repair(ServerRequest request){
        return ServerResponse.ok().build(
                messageReceivedService.repair()
        );

    }
}
