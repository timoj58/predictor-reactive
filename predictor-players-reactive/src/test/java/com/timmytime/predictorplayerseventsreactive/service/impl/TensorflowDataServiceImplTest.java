package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.model.StatMetric;
import com.timmytime.predictorplayerseventsreactive.repo.PlayerMatchRepo;
import com.timmytime.predictorplayerseventsreactive.service.TensorflowDataService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TensorflowDataServiceImplTest {

    private final PlayerMatchRepo playerMatchRepo = mock(PlayerMatchRepo.class);

    private final TensorflowDataService tensorflowDataService
            = new TensorflowDataServiceImpl(playerMatchRepo);

    @Test
    void load() throws InterruptedException {
        when(playerMatchRepo.findByDateAndPlayerId(any(), any()))
                .thenReturn(Optional.empty());

        tensorflowDataService.load(PlayerMatch.builder().build());

        Thread.sleep(100);

        verify(playerMatchRepo, atLeastOnce()).save(any());

    }

    @Test
    void dontLoad() throws InterruptedException {
        when(playerMatchRepo.findByDateAndPlayerId(any(), any()))
                .thenReturn(Optional.of(
                        PlayerMatch.builder().build()
                ));

        tensorflowDataService.load(PlayerMatch.builder().build());

        Thread.sleep(100);

        verify(playerMatchRepo, never()).save(any());

    }

    @Test
    void getPlayerCsv() {
        when(playerMatchRepo.findByDateBetween(any(), any()))
                .thenReturn(Arrays.asList(
                        PlayerMatch.builder()
                                .date(LocalDate.now())
                                .stats(Arrays.asList(
                                        StatMetric.builder().label("goals").value(0).build(),
                                        StatMetric.builder().label("assists").value(0).build(),
                                        StatMetric.builder().label("yellows").value(0).build()
                                ))
                                .home(true).build()
                ));

        var res = tensorflowDataService.getPlayerCsv("01-01-2020", "02-01-2020");

        assertFalse(res.isEmpty());
    }
}