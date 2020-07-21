package com.timmytime.predictoreventdatareactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<JsonNode> received);
    Mono<Void> completed();
}
