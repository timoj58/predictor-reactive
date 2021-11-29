package com.timmytime.predictorclientreactive.handler;

import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import com.timmytime.predictorclientreactive.service.StartupService;
import com.timmytime.predictorclientreactive.service.VocabService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class MessageHandler {

    private final MessageReceivedService messageReceivedService;
    private final VocabService vocabService;

    public Mono<ServerResponse> createVocab(ServerRequest request) {

        return ServerResponse.ok().build(vocabService.createVocab());
    }

    public Mono<ServerResponse> receive(ServerRequest request) {

        Mono<Message> message = request.bodyToMono(Message.class);
        return ServerResponse.ok().build(messageReceivedService.receive(message));
    }

}
