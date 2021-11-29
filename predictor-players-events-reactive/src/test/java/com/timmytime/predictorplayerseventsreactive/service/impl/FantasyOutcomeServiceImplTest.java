package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.repo.FantasyOutcomeRepo;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Disabled
class FantasyOutcomeServiceImplTest {

    private final FantasyOutcomeRepo fantasyOutcomeRepo = mock(FantasyOutcomeRepo.class);

    private final FantasyOutcomeService fantasyOutcomeService
            = new FantasyOutcomeServiceImpl(fantasyOutcomeRepo);

    @Test
    void topSelections(){

        var predictions = "[]";

        when(fantasyOutcomeRepo.findByCurrentAndFantasyEventType(anyBoolean(), any()))
                .thenReturn(
                        Flux.just(
                                FantasyOutcome.builder()
                                        .eventDate(LocalDateTime.now().minusDays(10))
                                        .prediction(predictions)
                                        .build(),
                                FantasyOutcome.builder()
                                        .eventDate(LocalDateTime.now().minusDays(2))
                                        .prediction(predictions)
                                        .build(),
                                FantasyOutcome.builder()
                                        .eventDate(LocalDateTime.now().minusDays(3))
                                        .prediction(predictions)
                                        .build()
                                )
                );

        var result = fantasyOutcomeService.topSelections(FantasyEventTypes.GOALS.name(), 70).collectList().block();

        assertTrue(result.size() == 2);
    }

}