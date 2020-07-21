package com.timmytime.predictordatareactive.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.timmytime.predictordatareactive.message.ResultMessage;
import com.timmytime.predictordatareactive.router.MessageFunction;
import com.timmytime.predictordatareactive.service.DataRepairService;
import com.timmytime.predictordatareactive.service.MessageReceivedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class MessageHandler {

    private final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final MessageReceivedService messageReceivedService;

    @Autowired
    public MessageHandler(
            MessageReceivedService messageReceivedService
    ) {
        this.messageReceivedService = messageReceivedService;
    }

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
}
