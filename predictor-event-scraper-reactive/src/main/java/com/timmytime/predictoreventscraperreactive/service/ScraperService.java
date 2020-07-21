package com.timmytime.predictoreventscraperreactive.service;

import reactor.core.publisher.Mono;

public interface ScraperService {
    Mono<Void> scrape();
}
