package com.timmytime.predictorteamsreactive.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.service.MessageReceivedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class MessageHandler {

    private final MessageReceivedService messageReceivedService;

    @Autowired
    public MessageHandler(
            MessageReceivedService messageReceivedService
    ){
        this.messageReceivedService = messageReceivedService;
    }

    public Mono<ServerResponse> receive(ServerRequest request) {

        Mono<Message> message = request.bodyToMono(Message.class);

        return ServerResponse.ok().build(
                messageReceivedService.receive(message)
        );
    }

    public Mono<ServerResponse> training(ServerRequest request) {

        return ServerResponse.ok().build(
                messageReceivedService.training(
                        UUID.fromString(request.queryParam("id").get())
                )
        );
    }

    public Mono<ServerResponse> initTraining(ServerRequest request) {

        return ServerResponse.ok().build(
                messageReceivedService.initTraining()
        );
    }

}
