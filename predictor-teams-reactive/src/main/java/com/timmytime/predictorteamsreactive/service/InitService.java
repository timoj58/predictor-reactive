package com.timmytime.predictorteamsreactive.service;

import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init(
            @RequestParam String from,
            @RequestParam String to
    );
}
