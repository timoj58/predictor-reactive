package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.EventOutcome;
import com.timmytime.predictorteamsreactive.model.Match;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TensorflowDataServiceImplTest {

    WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TensorflowDataServiceImpl tensorflowDataService
            = new TensorflowDataServiceImpl("events", "data",
            webClientFacade);

    @Test
    public void smoke() {
        tensorflowDataService.load(
                CountryMatch.builder()
                        .country("england")
                        .match(Match.builder()
                                .awayScore(1)
                                .homeScore(1)
                                .date(LocalDateTime.now()).build())
                        .build()
        );

        var startDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        var endDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        var csv = tensorflowDataService.getCountryCsv(
                "england", startDate, endDate
        );

        assertTrue(csv.size() == 1);

        tensorflowDataService.clear("england");

        csv = tensorflowDataService.getCountryCsv(
                "england", startDate, endDate
        );

        assertTrue(csv.isEmpty());

    }


    @Test
    public void loadOutstanding() {
        when(webClientFacade.getOutstandingEvents(anyString()))
                .thenReturn(Flux.just(EventOutcome.builder()
                        .competition("england_1")
                        .away(UUID.randomUUID())
                        .home(UUID.randomUUID())
                        .date(LocalDateTime.now()).build()));

        when(webClientFacade.getMatch(anyString()))
                .thenReturn(Mono.just(
                        Match.builder()
                                .awayTeam(UUID.randomUUID())
                                .awayTeam(UUID.randomUUID())
                                .homeScore(1)
                                .awayScore(1)
                                .date(LocalDateTime.now()).build()
                ));

        tensorflowDataService.loadOutstanding("england", () -> {
        });

        var startDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        var endDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        var csv = tensorflowDataService.getCountryCsv(
                "england", startDate, endDate
        );

        assertTrue(csv.size() == 1);

    }

}