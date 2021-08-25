package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.service.BookmakerService;
import com.timmytime.predictoreventscraperreactive.service.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service("scraperService")
public class ScraperServiceImpl implements ScraperService {

    private final EspnService espnService;

    @Override
    public Mono<Void> scrape() {
        log.info("scraping...");

        Mono.just(espnService)
        .subscribe(BookmakerService::scrape);

        return Mono.empty();

    }

}
