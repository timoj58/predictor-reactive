package com.timmytime.predictorclientreactive.handler;

import com.timmytime.predictorclientreactive.request.Message;
import com.timmytime.predictorclientreactive.service.MessageReceivedService;
import com.timmytime.predictorclientreactive.service.VocabService;
import com.timmytime.predictorclientreactive.service.impl.BetServiceImpl;
import com.timmytime.predictorclientreactive.service.impl.PlayersMatchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class MessageHandler {

    private final MessageReceivedService messageReceivedService;
    private final VocabService vocabService;
    private final BetServiceImpl betService;
    private final PlayersMatchServiceImpl playersMatchService;

    public Mono<ServerResponse> createVocab(ServerRequest request) {

        return ServerResponse.ok().build(
                vocabService.createVocab()
        );
    }

    public Mono<ServerResponse> receive(ServerRequest request) {

        Mono<Message> message = request.bodyToMono(Message.class);

        return ServerResponse.ok().build(
                messageReceivedService.receive(message)
        );
    }

    public Mono<ServerResponse> test(ServerRequest request) {
        return ServerResponse.ok().build(processTest());
    }

    private Mono<Void> processTest(){
        return Mono.just("test")
                .doOnNext(v -> {
                    CompletableFuture.runAsync(betService::load)
                            .thenRun(playersMatchService::load);
                })
                .thenEmpty(Mono.empty());
    }

}
