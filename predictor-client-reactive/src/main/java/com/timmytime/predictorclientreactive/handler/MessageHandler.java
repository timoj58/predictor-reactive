package com.timmytime.predictorclientreactive.handler;

import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class MessageHandler {

    private final MessageReceivedService messageReceivedService;


    public Mono<ServerResponse> test(ServerRequest request) {

        return ServerResponse.ok().build(
                messageReceivedService.test()
        );
    }

    public Mono<ServerResponse> receive(ServerRequest request) {

        Mono<Message> message = request.bodyToMono(Message.class);

        return ServerResponse.ok().build(
                messageReceivedService.receive(message)
        );
    }
}
