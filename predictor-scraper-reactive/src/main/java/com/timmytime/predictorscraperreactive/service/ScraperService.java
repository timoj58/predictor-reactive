package com.timmytime.predictorscraperreactive.service;

import reactor.core.publisher.Mono;

public interface ScraperService {
    Mono<Void> scrape();

    Mono<Void> historic();

    Mono<Void> init();

}
