package com.timmytime.predictormessagereactive.service;

import reactor.core.publisher.Flux;

public interface InitService {
    Flux<String> init();
}
