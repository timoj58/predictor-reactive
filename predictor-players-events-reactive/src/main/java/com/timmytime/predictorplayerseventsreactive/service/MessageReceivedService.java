package com.timmytime.predictorplayerseventsreactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);

    Mono<Void> prediction(@RequestParam UUID id, Mono<JsonNode> prediction);

}
