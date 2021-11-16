package com.timmytime.predictorteamsreactive.e2e;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.model.Message;
import com.timmytime.predictorteamsreactive.model.TrainingHistory;
import com.timmytime.predictorteamsreactive.repo.TrainingHistoryRepo;
import com.timmytime.predictorteamsreactive.service.*;
import com.timmytime.predictorteamsreactive.service.impl.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TeamsServiceTest {

    private final TrainingHistoryRepo trainingHistoryRepo = mock(TrainingHistoryRepo.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TrainingHistoryService trainingHistoryService
            = new TrainingHistoryServiceImpl(trainingHistoryRepo);

    private final TensorflowDataService tensorflowDataService
            = new TensorflowDataServiceImpl("events", "data", webClientFacade);

    private final TensorflowTrainService tensorflowTrainService
            = new TensorflowTrainServiceImpl("training", "results", "goals", webClientFacade);

    private final TrainingService trainingService
            = new TrainingServiceImpl("data", 0, false,
            tensorflowDataService, tensorflowTrainService, webClientFacade);
    private final TrainingModelService trainingModelService
            = new TrainingModelServiceImpl("data", 0, tensorflowDataService, tensorflowTrainService,
            trainingHistoryService, webClientFacade);

    private final MessageReceivedServiceImpl messageReceivedService
            = new MessageReceivedServiceImpl("events", "players", false,
            trainingHistoryService, trainingModelService, trainingService, tensorflowDataService, webClientFacade);

    @Test
    void training(){

        when(webClientFacade.getOutstandingEvents(anyString()))
                .thenReturn(Flux.empty());

        var history = Arrays.asList(
                TrainingHistory.builder()
                        .country("greece")
                        .id(UUID.randomUUID())
                        .fromDate(LocalDateTime.now().minusDays(10))
                        .type(Training.TRAIN_RESULTS)
                        .toDate(LocalDateTime.now().minusDays(2)).build());

        when(trainingHistoryRepo.findByTypeAndCountryOrderByDateDesc(any(), anyString()))
                .thenReturn(history);

        when(trainingHistoryRepo.findByTypeAndCountryOrderByDateDesc(any(), anyString()))
                .thenReturn(history);

        when(trainingHistoryRepo.save(any())).thenReturn(history.get(0));

        when(webClientFacade.getMatches(anyString()))
                .thenReturn(Flux.just(Match.builder().build()));

        messageReceivedService.receive(Mono.just(
                Message.builder().country("greece").competition("greece_1").build()
        )).subscribe();

        verify(webClientFacade, atLeastOnce()).train(anyString());
    }

}
