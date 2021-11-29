package com.timmytime.predictorplayerseventsreactive.service;

import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init();
}
