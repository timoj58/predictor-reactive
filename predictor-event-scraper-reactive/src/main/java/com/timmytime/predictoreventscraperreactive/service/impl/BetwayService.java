package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictoreventscraperreactive.factory.BetwayScraperFactory;
import com.timmytime.predictoreventscraperreactive.factory.BookmakerScraperConfigurationFactory;
import com.timmytime.predictoreventscraperreactive.service.BookmakerService;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("betwayService")
public class BetwayService implements BookmakerService {

    private final BookmakerScraperConfigurationFactory bookmakerScraperConfigurationFactory;
    private final BetwayScraperFactory betwayScraperFactory;
    private final MessageService messageService;

    private final Integer delay;

    @Autowired
    public BetwayService(
            @Value("${delays.betway}") Integer delay,
            BookmakerScraperConfigurationFactory bookmakerScraperConfigurationFactory,
            BetwayScraperFactory betwayScraperFactory,
            MessageService messageService
    ) {
        this.delay = delay;
        this.bookmakerScraperConfigurationFactory = bookmakerScraperConfigurationFactory;
        this.betwayScraperFactory = betwayScraperFactory;
        this.messageService = messageService;
    }

    @Override
    public void scrape() {
        log.info("scraping betway");

        List<BookmakerSiteRules> bookmakerSiteRules
                = bookmakerScraperConfigurationFactory.getConfig(ScraperTypeKeys.BETWAY_ODDS)
                .getBookmakerScrapers().stream().findFirst().get()
                .getSiteRules();

        List<BookmakerSiteRules> leagueRules
                = bookmakerSiteRules
                .stream()
                .filter(f -> !f.getId().equals("generic") && f.getType().equals("leagues") && f.getActive())
                .collect(Collectors.toList());

        List<String> leagues =
                leagueRules
                        .stream()
                        .sorted(Comparator.comparing(BookmakerSiteRules::getOrder))
                        .map(BookmakerSiteRules::getId)
                        .collect(Collectors.toList());


        BookmakerSiteRules eventRules = bookmakerSiteRules
                .stream()
                .filter(f -> f.getType().equals("events") && f.getId().equals("generic"))
                .findFirst()
                .get();

        //we dont have all the leagues available
        Flux.fromStream(CountryCompetitions.getAllCompetitions()
                .stream())
                .subscribe(competition -> {
                    if (!leagues.contains(competition)) {
                        messageService.send(ScraperTypeKeys.BETWAY_ODDS.name(), competition);
                    }
                });

        Flux.fromStream(
                leagues.stream()
        ).subscribe(league ->
                Mono.just(
                        leagueRules.stream().filter(f -> f.getId().equals(league)).findFirst().get()
                )
                        .delayElement(Duration.ofMinutes(delay))
                        .doOnNext(scraper ->
                                Flux.fromStream(
                                        betwayScraperFactory.getEventsScraper().scrape(
                                                bookmakerSiteRules
                                                        .stream()
                                                        .filter(f -> f.getType().equals("leagues") && f.getId().equals("generic"))
                                                        .findFirst()
                                                        .get()
                                                , scraper).stream()
                                ).delayElements(Duration.ofSeconds(5 * delay))
                                        .subscribe(id ->
                                                messageService.send(
                                                        betwayScraperFactory.getEventScraper().scrape(eventRules, new JSONObject().put("eventId", id), scraper.getId()))
                                        )
                        ).doFinally(end ->
                        Mono.just(ScraperTypeKeys.BETWAY_ODDS.name())
                                .delayElement(Duration.ofMinutes(5 * delay))
                                .subscribe(provider -> messageService.send(provider, league))
                ).subscribe()
        );

    }


}
