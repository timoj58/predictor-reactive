package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.CompetitionService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import com.timmytime.predictorteamsreactive.service.TrainingService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    private final CompetitionService competitionService = mock(CompetitionService.class);
    private final TrainingHistoryService trainingHistoryService = mock(TrainingHistoryService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final TrainingService trainingService = mock(TrainingService.class);

    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl(
                    "dummy",
            "dummy",
            competitionService,
            trainingHistoryService,
            trainingService,
            webClientFacade);

    @Test
    public void messageNotProcessedTest() throws InterruptedException {

        Message message = new Message();
        message.setCountry("england");
        message.setCompetition("england_1");

        messageReceivedService.receive(
                Mono.just(message)
        ).subscribe();

        Thread.sleep(1000L);

        verify(competitionService, never()).load(any());
    }


    @Test
    public void messageProcessedTest() throws InterruptedException {

        Message message = new Message();
        message.setCountry("italy");
        message.setCompetition("italy_1");

        Message message2 = new Message();
        message2.setCountry("italy");
        message2.setCompetition("italy_2");

        messageReceivedService.receive(
                Mono.just(message)
        ).subscribe();

        messageReceivedService.receive(
                Mono.just(message2)
        ).subscribe();

        Thread.sleep(1000L);

        verify(competitionService, atLeastOnce()).load(any());
        verify(trainingHistoryService, atLeastOnce()).create(any());
    }

    @Test
    public void trainingFinishedTest() throws InterruptedException {

        TrainingHistory trainingHistory = new TrainingHistory();
        trainingHistory.setCountry("england");

        when(trainingHistoryService.find(any(UUID.class))).thenReturn(trainingHistory);
        when(trainingHistoryService.finished()).thenReturn(Boolean.TRUE);

        messageReceivedService.training(UUID.randomUUID()).subscribe();

        Thread.sleep(1000L);

        verify(webClientFacade, atLeastOnce()).sendMessage(any(), any());

    }

    @Test
    public void trainingNotFinishedTest() throws InterruptedException {

        when(trainingHistoryService.find(any(UUID.class))).thenReturn(new TrainingHistory());
        when(trainingHistoryService.finished()).thenReturn(Boolean.FALSE);

        messageReceivedService.training(UUID.randomUUID()).subscribe();

        Thread.sleep(1000L);

        verify(webClientFacade, never()).sendMessage(any(), any());

    }

}