package com.timmytime.predictorplayerseventsreactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorplayerseventsreactive.request.Message;
import reactor.core.publisher.Mono;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<Message> message);

    Mono<Void> prediction(Mono<JsonNode> prediction);

}
