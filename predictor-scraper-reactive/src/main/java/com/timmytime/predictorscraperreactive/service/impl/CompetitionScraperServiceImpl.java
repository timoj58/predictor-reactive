package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.request.Message;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Service("competitionScraperService")
public class CompetitionScraperServiceImpl implements CompetitionScraperService {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory;
    private final ScraperFactory scraperFactory;
    private final MessageService messageService;
    private final Integer dayDelay;
    private final Integer matchDelay;

    @Autowired
    public CompetitionScraperServiceImpl(
            @Value("${delays.day}") Integer dayDelay,
            @Value("${delays.match}") Integer matchDelay,
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory,
            ScraperFactory scraperFactory,
            MessageService messageService
    ) {
        this.dayDelay = dayDelay;
        this.matchDelay = matchDelay;
        this.sportsScraperConfigurationFactory = sportsScraperConfigurationFactory;
        this.scraperFactory = scraperFactory;
        this.messageService = messageService;

    }

    @Override
    public void scrape(ScraperHistory scraperHistory, SiteRules competition) {
        log.info("scraping {}", competition.getId());

        //our date range...review time delays.  slow slow slow (also reduces errors too)
        Flux.fromStream(
                Stream.iterate(scraperHistory.getDate().minusDays(scraperHistory.getDaysScraped()), d -> d.plusDays(1))
                        .limit(scraperHistory.getDaysScraped())
        )
                .delayElements(Duration.ofSeconds(dayDelay/3))
                .doOnNext(date ->

                        Flux.fromStream(
                                scraperFactory.getResultScraper(
                                        sportsScraperConfigurationFactory
                                ).scrape(competition, date.toLocalDate()).stream()
                        ).delayElements(Duration.ofSeconds(matchDelay))
                                .subscribe(result -> {

                                            messageService.send(result);

                                            Flux.fromStream(
                                                    Stream.of(
                                                            scraperFactory.getMatchScraper(sportsScraperConfigurationFactory),
                                                            scraperFactory.getLineupScraper(sportsScraperConfigurationFactory)
                                                    )
                                            )
                                                    .subscribe(scrapers -> {
                                                        try {
                                                            messageService.send(scrapers.scrape(result.getMatchId()));
                                                        } catch (JsonProcessingException e) {
                                                            log.error("failed to process data", e);
                                                        }
                                                    });
                                        }
                                )
                ).doFinally(send ->
                Mono.just(competition.getId())
                        .delayElement(Duration.ofSeconds(dayDelay))
                        .subscribe(id -> messageService.send(new Message(competition.getId())))
        )
                .subscribe();

    }
}
