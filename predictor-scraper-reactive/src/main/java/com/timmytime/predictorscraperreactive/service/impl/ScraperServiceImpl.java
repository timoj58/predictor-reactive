package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.ScraperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;


@Slf4j
@Service("scraperService")
public class ScraperServiceImpl implements ScraperService {

    private final CompetitionScraperService competitionScraperService;
    private final ScraperHistoryRepo scraperHistoryRepo;

    @Autowired
    public ScraperServiceImpl(
            CompetitionScraperService competitionScraperService,
            ScraperHistoryRepo scraperHistoryRepo
    ) {
        this.competitionScraperService = competitionScraperService;
        this.scraperHistoryRepo = scraperHistoryRepo;
    }

    @Override
    public Mono<Void> scrape() {

        log.info("scrape started...");

        ScraperHistory scraperHistory = new ScraperHistory();

        scraperHistory.setId(UUID.randomUUID());
        scraperHistory.setDate(LocalDateTime.now());
        scraperHistory.setDaysScraped((int)
                Duration.between(
                        scraperHistoryRepo.findFirstByOrderByDateDesc().getDate().toLocalDate().atStartOfDay(),
                        LocalDate.now().atStartOfDay()
                ).toDays());

        scraperHistoryRepo.save(scraperHistory);
        process(scraperHistory);

        return Mono.empty();
    }

    @Override
    public Mono<Void> historic() {

        var historyStart = LocalDateTime.now().minusYears(12).minusMonths(2);

        ScraperHistory scraperHistory = new ScraperHistory();
        scraperHistory.setDate(LocalDateTime.now());

        scraperHistory.setDaysScraped((int) DAYS.between(historyStart.toLocalDate(), LocalDate.now()));

        log.info("scraping from {} with days to scrape {}", scraperHistory.getDate().toString(), scraperHistory.getDaysScraped());

        CompletableFuture.runAsync(() -> process(scraperHistory));
        return Mono.empty();
    }

    private void process(ScraperHistory scraperHistory) {
        competitionScraperService.setResultsInQueue(scraperHistory.getDaysScraped());
        Flux.fromStream(
                Stream.iterate(scraperHistory.getDate().minusDays(scraperHistory.getDaysScraped()), d -> d.plusDays(1))
                        .limit(scraperHistory.getDaysScraped())
        ).subscribe(competitionScraperService::scrape);

    }

    //need to init the history again, once historic finished.

}
