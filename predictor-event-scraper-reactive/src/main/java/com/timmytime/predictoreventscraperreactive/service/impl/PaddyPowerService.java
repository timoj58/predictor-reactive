package com.timmytime.predictoreventscraperreactive.service.impl;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictoreventscraperreactive.facade.ScraperProxyFacade;
import com.timmytime.predictoreventscraperreactive.factory.BookmakerScraperConfigurationFactory;
import com.timmytime.predictoreventscraperreactive.factory.PaddyPowerScraperFactory;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerAppKeyScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerEventSpecificScraper;
import com.timmytime.predictoreventscraperreactive.scraper.paddypower.PaddyPowerEventsScraper;
import com.timmytime.predictoreventscraperreactive.service.BookmakerService;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("paddyPowerService")
public class PaddyPowerService implements BookmakerService {

    private final Logger log = LoggerFactory.getLogger(PaddyPowerService.class);

    private final BookmakerScraperConfigurationFactory bookmakerScraperConfigurationFactory;
    private final PaddyPowerScraperFactory paddyPowerScraperFactory;
    private final ScraperProxyFacade scraperProxyFacade;
    private final MessageService messageService;
    private final Integer delay;
    private final Integer delay2;

    @Autowired
    public PaddyPowerService(
            @Value("${paddypower.delay}") Integer delay,
            @Value("${paddypower.delay.2}") Integer delay2,
            BookmakerScraperConfigurationFactory bookmakerScraperConfigurationFactory,
            PaddyPowerScraperFactory paddyPowerScraperFactory,
            ScraperProxyFacade scraperProxyFacade,
            MessageService messageService
    ){
        this.delay = delay;
        this.delay2 = delay2;
        this.bookmakerScraperConfigurationFactory = bookmakerScraperConfigurationFactory;
        this.paddyPowerScraperFactory = paddyPowerScraperFactory;
        this.scraperProxyFacade = scraperProxyFacade;
        this.messageService = messageService;
    }

    @Override
    public void scrape() {
        log.info("scraping paddypower");

        List<BookmakerSiteRules> bookmakerSiteRules
                = bookmakerScraperConfigurationFactory.getConfig(ScraperTypeKeys.PADDYPOWER_ODDS)
                .getBookmakerScrapers().stream().findFirst().get()
                .getSiteRules();

        List<BookmakerSiteRules> appKeys =  bookmakerSiteRules
                .stream()
                .filter(f -> f.getType().equals("app-key"))
                .collect(Collectors.toList());

        BookmakerSiteRules events =bookmakerSiteRules
                .stream()
                .filter(f -> f.getType().equals("competition-events")
                && f.getId().equals("event-odds"))
                .findFirst()
                .get();

        //we dont have all the leagues available
        Flux.fromStream(CountryCompetitions.getAllCompetitions()
                .stream())
                .subscribe(competition -> {
                    if(!appKeys.stream().map(BookmakerSiteRules::getId).collect(Collectors.toList()).contains(competition)){
                        messageService.send(ScraperTypeKeys.PADDYPOWER_ODDS.name(), competition);
                    }
                });



        Flux.fromStream(appKeys.stream())
                .sort(Comparator.comparing(BookmakerSiteRules::getOrder))
                .delayElements(Duration.ofMinutes(delay))
                .subscribe(
                        appKey -> {
                            String key = paddyPowerScraperFactory.getAppKeyScraper(scraperProxyFacade).scrape(appKey);

                            Mono.just(
                                    bookmakerSiteRules
                                            .stream().filter(f -> f.getType().equals("competitions")
                                            && f.getId().equals(appKey.getId()) && f.getActive())
                                    .findFirst().get()
                            ).delayElement(Duration.ofSeconds(delay2))
                                    .doOnNext(competition -> {
                                        PaddyPowerEventsScraper paddyPowerEventsScraper =
                                                paddyPowerScraperFactory.getEventsScraper(scraperProxyFacade);

                                        PaddyPowerEventSpecificScraper paddyPowerEventSpecificScraper
                                              =  paddyPowerScraperFactory.getEventScraper(scraperProxyFacade);

                                        JSONObject data = paddyPowerEventsScraper.scrape(competition, key);
                                        data.put("appKey", key);

                                          messageService.send(
                                                  paddyPowerEventSpecificScraper.scrape(events, data, competition.getId())
                                          );
                                    })
                                    .doFinally(end ->
                                            Mono.just(ScraperTypeKeys.PADDYPOWER_ODDS.name())
                                                    .delayElement(Duration.ofSeconds(30))
                                                    .subscribe(provider -> messageService.send(provider, appKey.getId())))
                                    .subscribe();

                        });
    }

}
