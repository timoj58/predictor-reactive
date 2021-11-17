package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyEvent;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.repo.FantasyOutcomeRepo;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FantasyOutcomeServiceImplTest {

    private final FantasyOutcomeRepo fantasyOutcomeRepo = mock(FantasyOutcomeRepo.class);
    private final PlayerService playerService = mock(PlayerService.class);

    private final FantasyOutcomeService fantasyOutcomeService
            = new FantasyOutcomeServiceImpl(fantasyOutcomeRepo, playerService);

    @Test
    void topSelections(){
        when(fantasyOutcomeRepo.findByCurrentAndFantasyEventType(anyBoolean(), any()))
                .thenReturn(
                        Flux.just(
                                FantasyOutcome.builder().build(),
                                FantasyOutcome.builder().build(),
                                FantasyOutcome.builder().build()
                                )
                );

        var result = fantasyOutcomeService.topSelections(FantasyEventTypes.GOALS.name(), 70).collectList().block();
    }
}