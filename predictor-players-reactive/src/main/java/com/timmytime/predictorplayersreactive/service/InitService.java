package com.timmytime.predictorplayersreactive.service;

import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface InitService {

    Mono<Void> init(
            @RequestParam(defaultValue = "01-08-2009") String from,
            @RequestParam(defaultValue = "01-08-2009") String to
    );
}
