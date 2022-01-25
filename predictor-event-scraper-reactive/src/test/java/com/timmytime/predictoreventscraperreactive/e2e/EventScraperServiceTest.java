package com.timmytime.predictoreventscraperreactive.e2e;

import com.timmytime.predictoreventscraperreactive.configuration.CompetitionFixtures;
import com.timmytime.predictoreventscraperreactive.configuration.FixturesScraperConfiguration;
import com.timmytime.predictoreventscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictoreventscraperreactive.facade.WebClientFacade;
import com.timmytime.predictoreventscraperreactive.scraper.CompetitionFixtureScraper;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import com.timmytime.predictoreventscraperreactive.service.ScraperService;
import com.timmytime.predictoreventscraperreactive.service.impl.EspnService;
import com.timmytime.predictoreventscraperreactive.service.impl.MessageServiceImpl;
import com.timmytime.predictoreventscraperreactive.service.impl.ScraperServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class EventScraperServiceTest {

    private final FixturesScraperConfiguration fixturesScraperConfiguration = mock(FixturesScraperConfiguration.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final MessageService messageService
            = new MessageServiceImpl("eventsData", "events", webClientFacade);
    private final CompetitionFixtureScraper competitionFixtureScraper
            = new CompetitionFixtureScraper(messageService);


    private final EspnService espnService
            = new EspnService(fixturesScraperConfiguration, competitionFixtureScraper);

    private final ScraperService scraperService
            = new ScraperServiceImpl(espnService);

    @Test
    void scrape() throws InterruptedException {

        when(fixturesScraperConfiguration.getCompetitionFixtures())
                .thenReturn(Arrays.asList(
                        CompetitionFixtures.builder()
                                .code(CompetitionFixtureCodes.ENGLAND_1)
                                .url("https://www.espn.co.uk/soccer/fixtures/_/date/{date}/league/eng.1"
                                        .replace("{date}", LocalDate.now().minusDays(10).format(
                                                DateTimeFormatter.ofPattern("yyyyMMdd")
                                        ))).build()
                ));

        scraperService.scrape().subscribe();

        Thread.sleep(3000);
        verify(webClientFacade, atLeastOnce()).send(anyString(), any());
    }
}
