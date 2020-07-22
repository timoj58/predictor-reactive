package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.Message;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);
    Mono<Void> training(UUID id);
    Mono<Void> historicTraining(UUID id);
}
