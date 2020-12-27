package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.model.Match;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidationServiceImplTest {

    private final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final ValidationServiceImpl validationService
            = new ValidationServiceImpl("", eventOutcomeService, webClientFacade);

    @Test
    public void validateResult() {

        EventOutcome eventOutcome = new EventOutcome();
        eventOutcome.setEventType(Predictions.PREDICT_GOALS.name());
        eventOutcome.setDate(LocalDateTime.now());
        eventOutcome.setPrediction("{\"result\":[{\"score\":83.4,\"key\":\"0\"},{\"score\":13.9,\"key\":\"1\"},{\"score\":2.1,\"key\":\"6\"},{\"score\":0.4,\"key\":\"5\"},{\"score\":0.2,\"key\":\"2\"},{\"score\":0,\"key\":\"11\"},{\"score\":0,\"key\":\"12\"},{\"score\":0,\"key\":\"0\"},{\"score\":0,\"key\":\"3\"},{\"score\":0,\"key\":\"7\"},{\"score\":0,\"key\":\"8\"},{\"score\":0,\"key\":\"9\"},{\"score\":0,\"key\":\"10\"}],\"away\":\"Peterhead\",\"type\":\"PREDICT_GOALS\",\"home\":\"Edinburgh City\"}");

        EventOutcome eventOutcome2 = new EventOutcome();
        eventOutcome2.setEventType(Predictions.PREDICT_RESULTS.name());
        eventOutcome2.setDate(LocalDateTime.now());
        eventOutcome2.setPrediction("{\"result\":[{\"score\":99.7,\"key\":\"homeWin\"},{\"score\":0.2,\"key\":\"draw\"},{\"score\":0.1,\"key\":\"awayWin\"}],\"away\":\"Charlton Athletic\",\"type\":\"PREDICT_RESULTS\",\"home\":\"Portsmouth\"}");

        when(eventOutcomeService.toValidate(anyString())).thenReturn(
                Flux.fromStream(
                        Arrays.asList(eventOutcome, eventOutcome2).stream()
                )
        );

        when(eventOutcomeService.save(any())).thenReturn(Mono.just(new EventOutcome()));

        Match match = new Match();
        match.setAwayScore(0);
        match.setHomeScore(1);

        when(webClientFacade.getMatch(any())).thenReturn(
                Mono.just(match)
        );

        validationService.validate(anyString());


        verify(eventOutcomeService, atLeastOnce()).save(eventOutcome);
        verify(eventOutcomeService, atLeastOnce()).save(eventOutcome2);


    }

}