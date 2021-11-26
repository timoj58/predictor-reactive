package com.timmytime.predictordatareactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init();
}
