package com.timmytime.predictorteamsreactive.service;

import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init();
}
