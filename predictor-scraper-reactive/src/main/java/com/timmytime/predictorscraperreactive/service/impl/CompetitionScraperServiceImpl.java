package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.configuration.ResultsConfiguration;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.model.ScraperModel;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.MatchScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service("competitionScraperService")
public class CompetitionScraperServiceImpl implements CompetitionScraperService {

    private final ScraperFactory scraperFactory;
    private final MessageService messageService;
    private final MatchScraperService matchScraperService;
    private final ResultsConfiguration resultsConfiguration;

    private Consumer<Triple<CompetitionFixtureCodes, String, LocalDate>> results;

    @Autowired
    public CompetitionScraperServiceImpl(
            ScraperFactory scraperFactory,
            MessageService messageService,
            MatchScraperService matchScraperService,
            ResultsConfiguration resultsConfiguration
    ) {
        this.scraperFactory = scraperFactory;
        this.messageService = messageService;
        this.matchScraperService = matchScraperService;
        this.resultsConfiguration = resultsConfiguration;

        Flux<Triple<CompetitionFixtureCodes, String, LocalDate>> results = Flux.push(sink ->
                CompetitionScraperServiceImpl.this.results = sink::next, FluxSink.OverflowStrategy.BUFFER);

        results.delayElements(Duration.ofMillis(100)).subscribe(this::process);
    }

    @Override
    public void scrape(LocalDateTime date) {
        Flux.fromStream(
                resultsConfiguration.getUrls().stream()
        ).subscribe(competition -> consume(Triple.of(competition.getLeft(), competition.getRight(), date.toLocalDate())));
    }

    private void consume(Triple<CompetitionFixtureCodes, String, LocalDate> config) {
        scraperFactory.getScraperTrackerService().addMatchesInQueue(config.getLeft());
        results.accept(config);
    }

    private void process(Triple<CompetitionFixtureCodes, String, LocalDate> config) {

        var matches = scraperFactory.getResultScraper().scrape(Pair.of(config.getLeft(), config.getMiddle()), config.getRight());

        scraperFactory.getScraperTrackerService().addMatches(
                config.getLeft(),
                matches.stream()
                        .map(ScraperModel::getMatchId)
                        .collect(Collectors.toList())
        );

        Flux.fromStream(matches.stream())
                .map(result -> Pair.of(config.getLeft(), messageService.send(result)))
                .subscribe(matchScraperService::add);
    }


    @Scheduled(fixedRate = 60000)
    private void retry() {
        Flux.fromStream(scraperFactory.getScraperTrackerService().getFailedResultsRequests().stream())
                .subscribe(this::consume);
    }
}
