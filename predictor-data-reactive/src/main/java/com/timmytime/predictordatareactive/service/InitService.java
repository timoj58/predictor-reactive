package com.timmytime.predictordatareactive.service;

import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init();
}
