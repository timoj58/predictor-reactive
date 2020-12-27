package com.timmytime.predictorscraperreactive.service;

import reactor.core.publisher.Mono;

public interface ScraperService {
    Mono<Void> scrape();
}
