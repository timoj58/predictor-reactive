package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.service.TensorflowDataService;
import com.timmytime.predictorteamsreactive.service.TrainingHistoryService;
import com.timmytime.predictorteamsreactive.service.TrainingModelService;
import com.timmytime.predictorteamsreactive.service.TrainingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class MessageReceivedServiceImplTest {

    TrainingHistoryService trainingHistoryService = mock(TrainingHistoryService.class);
    TrainingModelService trainingModelService = mock(TrainingModelService.class);
    TrainingService trainingService = mock(TrainingService.class);
    TensorflowDataService tensorflowDataService = mock(TensorflowDataService.class);
    WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl("events", false,
            trainingHistoryService, trainingModelService, trainingService, tensorflowDataService, webClientFacade);

    @Test
    void receiveNotStart() {
        messageReceivedService.receive(Mono.just(Message.builder()
                .eventType("ENGLAND").build())).subscribe();

        messageReceivedService.receive(Mono.just(Message.builder()
                .eventType("GREECE").build())).subscribe();

        verify(tensorflowDataService, atMostOnce()).loadOutstanding(anyString(), any());
    }


    @Test
    void receiveStart() {
        messageReceivedService.receive(Mono.just(Message.builder()
                .eventType("GREECE").build())).subscribe();

        verify(tensorflowDataService, atLeastOnce()).loadOutstanding(anyString(), any());
    }


    @Test
    void create() {
        messageReceivedService.createTrainingModels();
        verify(trainingModelService, atLeastOnce()).create();
    }

    @Test
    void trainingNext() {
        var id = UUID.randomUUID();
        when(trainingHistoryService.find(id)).thenReturn(TrainingHistory.builder()
                .toDate(LocalDateTime.now().plusDays(1))
                .type(Training.TRAIN_RESULTS).build());

        messageReceivedService.training(id).subscribe();
        verify(trainingService, atLeastOnce()).train(any());
    }

    @Test
    void trainingComplete() {
        var id = UUID.randomUUID();
        when(trainingHistoryService.find(id)).thenReturn(TrainingHistory.builder()
                .toDate(LocalDateTime.now().plusDays(1))
                .country("country")
                .type(Training.TRAIN_GOALS).build());

        messageReceivedService.training(id).subscribe();

        verify(trainingService, never()).train(any());
        verify(tensorflowDataService, atLeastOnce()).clear("country");
        verify(webClientFacade, atLeastOnce()).sendMessage(anyString(), any());
    }


}