package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.service.MatchScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.function.Consumer;

@Slf4j
@Service
public class MatchScraperServiceImpl implements MatchScraperService {

    private final ScraperFactory scraperFactory;
    private final MessageService messageService;
    private Consumer<Pair<CompetitionFixtureCodes, Integer>> matches;

    @Autowired
    public MatchScraperServiceImpl(
            ScraperFactory scraperFactory,
            MessageService messageService
    ) {
        this.scraperFactory = scraperFactory;
        this.messageService = messageService;

        Flux<Pair<CompetitionFixtureCodes, Integer>> results = Flux.push(sink ->
                MatchScraperServiceImpl.this.matches = sink::next, FluxSink.OverflowStrategy.BUFFER);

        results.delayElements(Duration.ofMillis(100)).subscribe(this::scrape);
    }

    @Override
    public void add(Pair<CompetitionFixtureCodes, Integer> matchId) {
        matches.accept(matchId);
    }


    private void scrape(Pair<CompetitionFixtureCodes, Integer> matchId) {
        scraperFactory.getPlayerScraper().scrape(matchId)
                .ifPresent(match -> {
                    scraperFactory.getScraperTrackerService().removeMatch(matchId);
                    messageService.send(match);
                });
    }

    @Scheduled(fixedRate = 60000)
    private void retry() {
        Flux.fromStream(scraperFactory.getScraperTrackerService().getFailedPlayersRequests().stream())
                .subscribe(this::add);
    }

}
