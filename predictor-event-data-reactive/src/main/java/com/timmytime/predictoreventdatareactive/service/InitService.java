package com.timmytime.predictoreventdatareactive.service;

import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init();
}
