package com.timmytime.predictorscraperreactive.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictorscraperreactive.configuration.ResultsConfiguration;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.factory.ScraperFactory;
import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import com.timmytime.predictorscraperreactive.repo.ScraperHistoryRepo;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import com.timmytime.predictorscraperreactive.service.impl.*;
import com.timmytime.predictorscraperreactive.util.MatchTracker;
import com.timmytime.predictorscraperreactive.util.FailedRequestTracker;
import com.timmytime.predictorscraperreactive.util.TrackerMetrics;
import com.timmytime.predictorscraperreactive.util.TripSwitch;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ScraperServiceTest {

    private final ResultsConfiguration resultsConfiguration = mock(ResultsConfiguration.class);
    private final ScraperHistoryRepo scraperHistoryRepo = mock(ScraperHistoryRepo.class);
    private final TrackerMetrics trackerMetrics = new TrackerMetrics(
            90
    );
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final MessageServiceImpl messageService
            = new MessageServiceImpl("data", "team", webClientFacade);
    private final ScraperTrackerService scraperTrackerService
            = new ScraperTrackerServiceImpl(messageService, trackerMetrics,
            new TripSwitch(), new MatchTracker(), new FailedRequestTracker());
    private final ScraperFactory scraperFactory = new ScraperFactory(
            "https://www.espn.co.uk/football/match/_/gameId/{game_id}",
            scraperTrackerService);
    private final PageServiceImpl pageService = new PageServiceImpl(
            scraperFactory, messageService);
    private final CompetitionScraperServiceImpl competitionScraperService
            = new CompetitionScraperServiceImpl(scraperFactory, pageService, resultsConfiguration);
    private final ScraperServiceImpl scraperService
            = new ScraperServiceImpl(competitionScraperService, scraperHistoryRepo);

    @Test
    void smoke() {

        when(resultsConfiguration.getUrls())
                .thenReturn(Arrays.asList(
                        Pair.of(CompetitionFixtureCodes.ENGLAND_4, "https://www.espn.co.uk/soccer/scoreboard/_/league/eng.4/date/{date}")
                ));

        when(scraperHistoryRepo.findFirstByOrderByDateDesc())
                .thenReturn(Optional.of(
                        ScraperHistory.builder()
                                .date(LocalDateTime.now().minusDays(10)).build()));

        scraperService.scrape().subscribe();
        verify(webClientFacade, atLeastOnce()).send(anyString(), any(JsonNode.class));
    }

}
