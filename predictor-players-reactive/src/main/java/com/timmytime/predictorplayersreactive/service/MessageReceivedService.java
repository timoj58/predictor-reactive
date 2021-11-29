package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.request.Message;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);

    Mono<Void> training(@RequestParam UUID id);

    Mono<Void> createTrainingModel();
}
