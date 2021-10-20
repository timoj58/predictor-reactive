package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.configuration.ResultsConfiguration;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Slf4j
@Service("competitionScraperService")
public class CompetitionScraperServiceImpl implements CompetitionScraperService {

    private final ScraperFactory scraperFactory;
    private final PageService pageService;
    private final ResultsConfiguration resultsConfiguration;

    @Autowired
    public CompetitionScraperServiceImpl(
            ScraperFactory scraperFactory,
            PageService pageService,
            ResultsConfiguration resultsConfiguration
    ) {
        this.scraperFactory = scraperFactory;
        this.pageService = pageService;
        this.resultsConfiguration = resultsConfiguration;
    }

    @Override
    public void scrape(LocalDateTime date) {
        Flux.fromStream(
                resultsConfiguration.getUrls().stream()
        ).subscribe(competition -> consume(scraperFactory.getResultScraper().createRequest(competition, date.toLocalDate()))
        );
    }

    @Override
    public void setResultsInQueue(int total) {
        Flux.fromArray(CompetitionFixtureCodes.values())
                .subscribe(competition -> scraperFactory.getScraperTrackerService().addResultsInQueue(competition, total));
    }

    private void consume(Triple<CompetitionFixtureCodes, ScraperType, String> request) {
        pageService.addPageRequest(request);
    }

    @Scheduled(fixedRateString = "${scheduler.retry}")
    private void retry() {
        Flux.fromStream(scraperFactory.getScraperTrackerService().getFailedRequests().stream())
                .subscribe(this::consume);
    }
}
