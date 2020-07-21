package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.configuration.SportScraper;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.CompetitionScraperService;
import com.timmytime.predictorscraperreactive.service.MessageService;
import com.timmytime.predictorscraperreactive.service.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.UUID;

@Service("scraperService")
public class ScraperServiceImpl implements ScraperService {

    private final Logger log = LoggerFactory.getLogger(ScraperServiceImpl.class);

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory;
    private final CompetitionScraperService competitionScraperService;
    private final ScraperHistoryRepo scraperHistoryRepo;
    private final LocalDateTime defaultStartDate;
    private final Integer dayDelay;
    private final MessageService messageService;

    @Autowired
    public ScraperServiceImpl(
            @Value("${day.delay}") Integer dayDelay,
            @Value("${results.start-date}") String defaultStartDate,
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory,
            CompetitionScraperService competitionScraperService,
            ScraperHistoryRepo scraperHistoryRepo,
            MessageService messageService
    ) {
        this.dayDelay = dayDelay;
        this.defaultStartDate = LocalDate.parse(
                defaultStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )
                .atStartOfDay();
        this.sportsScraperConfigurationFactory = sportsScraperConfigurationFactory;
        this.competitionScraperService = competitionScraperService;
        this.scraperHistoryRepo = scraperHistoryRepo;
        this.messageService = messageService;
    }


    @Override
    public Mono<Void> scrape() {

        log.info("scrape started...");

        ScraperHistory scraperHistory = new ScraperHistory();

        scraperHistory.setId(UUID.randomUUID());
        scraperHistory.setDate(LocalDateTime.now());
        scraperHistory.setDaysScraped(daysToScrape(scraperHistoryRepo.findFirstByOrderByDateDesc().getDate())); //needs to be configurvale

        scraperHistoryRepo.save(scraperHistory);

        SportScraper sportScraper =sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.RESULTS)
                .getSportScrapers()
                .stream()
                .findFirst()
                .get();

        Mono.just(sportScraper)//there is only one...
        .doOnNext(sport ->
                        Flux.fromStream(sport.getSiteRules()
                                .stream()
                                .filter(f -> f.getActive()
                                        &&      //get all none generic ones.
                                        !f.getId().equals("generic")
                                )
                        )
                                .sort(Comparator.comparing(SiteRules::getOrder))
                                .delayElements(Duration.ofSeconds(dayDelay * scraperHistory.getDaysScraped()+1))
                                .subscribe(competition -> competitionScraperService.scrape(
                                        scraperHistory,
                                        competition))
                )
                .doFinally(send ->
                        Mono.just("result")
                                .delayElement(Duration.ofSeconds(
                                        (dayDelay * scraperHistory.getDaysScraped()+1) + (
                                        sportScraper.getSiteRules()
                                        .stream()
                                        .filter(f -> !f.getId().equals("generic"))
                                        .filter(f -> f.getActive() == Boolean.TRUE)
                                        .count() *
                                                (dayDelay * scraperHistory.getDaysScraped()+1))))
                                .subscribe(message -> messageService.send())
                ).subscribe();


        return Mono.empty();
    }

    //this is not needed now....good...no more updating of config either.
    @PostConstruct
    public void init() {
        if (scraperHistoryRepo.count() == 0L) {
            //we need to add our start record if it does not exist.
            ScraperHistory scraperHistory = new ScraperHistory();
            scraperHistory.setId(UUID.randomUUID());
            scraperHistory.setDate(defaultStartDate);
            scraperHistory.setDaysScraped(0);

            scraperHistoryRepo.save(scraperHistory);
        }

    }

    private Integer daysToScrape(LocalDateTime date) {
        if (date.getDayOfWeek()
                .equals(DayOfWeek.TUESDAY)) {
            return 4;
        }
        return 3;
    }
}
