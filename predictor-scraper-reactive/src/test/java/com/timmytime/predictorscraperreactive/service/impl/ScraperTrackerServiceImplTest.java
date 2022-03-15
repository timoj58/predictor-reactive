package com.timmytime.predictorscraperreactive.service.impl;

import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import com.timmytime.predictorscraperreactive.util.RequestTracker;
import com.timmytime.predictorscraperreactive.util.TrackerMetrics;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class ScraperTrackerServiceImplTest {

    @SpyBean
    private RequestTracker requestTracker;

    @SpyBean
    private TrackerMetrics trackerMetrics;

    @Autowired
    private ScraperTrackerService service;

    @Test
    void addFailedMatches(){
        service.addFailedResultsRequest(Triple.of(null, null, null));

        verify(trackerMetrics, atLeastOnce()).addFailedResultsRequest(any());
        verify(requestTracker, atLeastOnce()).addFailedResultsRequest(any());

    }


    @Test
    void addFailedPlayers(){
        service.addFailedPlayersRequest(Triple.of(null, null, null));

        verify(trackerMetrics, atLeastOnce()).addFailedPlayersRequest(any());
        verify(requestTracker, atLeastOnce()).addFailedPlayersRequest(any());

    }


}