package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.Message;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);
    Mono<Void> training(@RequestParam UUID id);
    Mono<Void> initTraining();
}
