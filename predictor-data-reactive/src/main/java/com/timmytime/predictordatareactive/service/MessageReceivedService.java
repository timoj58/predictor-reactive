package com.timmytime.predictordatareactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictordatareactive.message.ResultMessage;
import com.timmytime.predictordatareactive.model.Result;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface MessageReceivedService {
    Mono<Void> receive(Mono<JsonNode> received);
    Mono<Void> completed();
}
