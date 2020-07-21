package com.timmytime.predictorscraperreactive.service;

import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ScraperService {
    Mono<Void> scrape();
}
