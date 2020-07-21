package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerScraper;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictoreventscraperreactive.factory.BookmakerScraperConfigurationFactory;
import com.timmytime.predictoreventscraperreactive.service.BookmakerService;
import com.timmytime.predictoreventscraperreactive.service.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service("scraperService")
public class ScraperServiceImpl implements ScraperService {

    private final Logger log = LoggerFactory.getLogger(ScraperServiceImpl.class);

    private final BookmakerService paddyPowerService;
    private final BookmakerService betwayService;

    @Autowired
    public ScraperServiceImpl(
            BookmakerService paddyPowerService,
            BookmakerService betwayService
    ){
        this.paddyPowerService = paddyPowerService;
        this.betwayService = betwayService;
    }

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
