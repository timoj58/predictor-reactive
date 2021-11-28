package com.timmytime.predictorplayerseventsreactive.service;

import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init();
}
