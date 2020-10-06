package com.timmytime.predictoreventsreactive.service;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import reactor.core.publisher.Flux;

public interface ValidationService {
    void validate(String county);
    Flux<EventOutcome> resetLast(String country);
}
