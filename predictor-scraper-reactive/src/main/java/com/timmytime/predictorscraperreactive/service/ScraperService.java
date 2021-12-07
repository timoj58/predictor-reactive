package com.timmytime.predictorscraperreactive.service;

import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface ScraperService {
    Mono<Void> scrape();

    Mono<Void> historic();

    Mono<Void> init(@RequestParam String from, @RequestParam String to);

}
