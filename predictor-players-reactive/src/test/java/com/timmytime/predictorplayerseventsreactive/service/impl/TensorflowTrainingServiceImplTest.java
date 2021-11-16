package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.PlayersTrainingHistory;
import com.timmytime.predictorplayerseventsreactive.service.PlayersTrainingHistoryService;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowTrainingService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TensorflowTrainingServiceImplTest {
    private final PlayersTrainingHistoryService playersTrainingHistoryService = mock(PlayersTrainingHistoryService.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TensorflowTrainingService tensorflowTrainingService
            = new TensorflowTrainingServiceImpl("training", "goals/<from>/<to>", "assists", "yellows",
            playersTrainingHistoryService, webClientFacade);

    @Test
    void train() {

        var id = UUID.randomUUID();

        var now = LocalDateTime.now();
        var formatted = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        when(playersTrainingHistoryService.find(id)).thenReturn(
                Mono.just(PlayersTrainingHistory.builder()
                        .fromDate(now)
                        .toDate(now)
                        .type(FantasyEventTypes.GOALS).build())
        );

        tensorflowTrainingService.train(id);

        verify(webClientFacade, atLeastOnce()).train("traininggoals/"+formatted+"/"+formatted);
    }

}