package com.timmytime.predictoreventsreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.enumerator.Messages;
import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.model.Match;
import com.timmytime.predictoreventsreactive.request.Message;
import com.timmytime.predictoreventsreactive.service.*;
import lombok.Getter;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final PredictionService predictionService = mock(PredictionService.class);
    private final PredictionResultService predictionResultService = mock(PredictionResultService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final EventOutcomeService eventOutcomeService = mock(EventOutcomeService.class);
    private final PredictionMonitorService predictionMonitorService = mock(PredictionMonitorService.class);
    private final ValidationService validationService =
            new ValidationServiceImpl("data", eventOutcomeService,webClientFacade);

    private final MessageReceivedService messageReceivedService
            = new MessageReceivedServiceImpl("players", 0,
            predictionService, predictionResultService, predictionMonitorService,
            validationService, webClientFacade);

    @Test
    void receive() throws InterruptedException {

        when(eventOutcomeService.lastEvents("GREECE"))
                .thenReturn(Flux.just(EventOutcome.builder().build()));

        when(eventOutcomeService.save(any())).thenReturn(Mono.just(EventOutcome.builder().build()));
        when(eventOutcomeService.toValidate("GREECE"))
                .thenReturn(Flux.just(EventOutcome.builder()
                        .eventType(Predictions.PREDICT_RESULTS.name())
                        .date(LocalDateTime.now())
                        .prediction("{\"result\":[{\"score\":83.4,\"key\":\"0\"},{\"score\":13.9,\"key\":\"1\"},{\"score\":2.1,\"key\":\"6\"},{\"score\":0.4,\"key\":\"5\"},{\"score\":0.2,\"key\":\"2\"},{\"score\":0,\"key\":\"11\"},{\"score\":0,\"key\":\"12\"},{\"score\":0,\"key\":\"0\"},{\"score\":0,\"key\":\"3\"},{\"score\":0,\"key\":\"7\"},{\"score\":0,\"key\":\"8\"},{\"score\":0,\"key\":\"9\"},{\"score\":0,\"key\":\"10\"}],\"away\":\"Peterhead\",\"type\":\"PREDICT_GOALS\",\"home\":\"Edinburgh City\"}")
                        .build()));
        when(webClientFacade.getMatch(any())).thenReturn(Mono.just(Match.builder().build()));

        Stream.of(Messages.values())
                .forEach(msg ->
                        messageReceivedService.receive(
                                Mono.just(Message.builder()
                                        .country("GREECE")
                                        .type(msg).build())
                        ).subscribe());

        Thread.sleep(250);

        verify(predictionService, atLeastOnce()).start("GREECE");
        verify(predictionMonitorService, atLeastOnce()).addCountry(CountryCompetitions.GREECE);

    }

    @Test
    void prediction() throws JsonProcessingException {

        messageReceivedService.prediction(UUID.randomUUID(), Mono.just(
                new ObjectMapper().readTree(new JSONObject().toString())
        )).subscribe();

        verify(predictionResultService, atLeastOnce()).result(any(), any());

    }
}