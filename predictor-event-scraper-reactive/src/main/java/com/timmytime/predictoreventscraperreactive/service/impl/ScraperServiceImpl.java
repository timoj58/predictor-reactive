package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.service.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service("scraperService")
public class ScraperServiceImpl implements ScraperService {

    private final PaddyPowerService paddyPowerService;
    private final BetwayService betwayService;

    @Override
    public Mono<Void> scrape() {
        log.info("scraping");

        Flux.fromStream(
                Arrays.asList(
                        paddyPowerService,
                        betwayService
                ).stream()
        ).subscribe(
                bookmakerService -> bookmakerService.scrape()
        );


        return Mono.empty();

    }

}
