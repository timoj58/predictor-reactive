package com.timmytime.predictorclientreactive.service;

import reactor.core.publisher.Mono;

public interface StartupService {
    Mono<Void> conduct();
}
