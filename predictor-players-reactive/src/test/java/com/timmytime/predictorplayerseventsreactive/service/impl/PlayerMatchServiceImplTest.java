package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.facade.WebClientFacade;
import com.timmytime.predictorplayerseventsreactive.model.LineupPlayer;
import com.timmytime.predictorplayerseventsreactive.model.Match;
import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.model.StatMetric;
import com.timmytime.predictorplayerseventsreactive.service.PlayerMatchService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PlayerMatchServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final PlayerMatchService playerMatchService
            = new PlayerMatchServiceImpl("data", webClientFacade);

    @Test
    void getAppearances() {
        var id = UUID.randomUUID();

        when(webClientFacade.getAppearances(anyString())).thenReturn(
                Flux.just(LineupPlayer.builder().player(id).build())
        );

        assertTrue(playerMatchService.getAppearances(id).blockFirst().getPlayer() == id);
    }

    @Test
    void getMatch() {
        var id = UUID.randomUUID();

        when(webClientFacade.getMatch(anyString())).thenReturn(
                Mono.just(Match.builder().id(id).build())
        );

        assertTrue(playerMatchService.getMatch(id).block().getId() == id);
    }

    @Test
    void getStats() {
        var id = UUID.randomUUID();
        var id2 = UUID.randomUUID();

        when(webClientFacade.getStats(anyString())).thenReturn(
                Flux.just(StatMetric.builder().id(id).build())
        );

        assertTrue(playerMatchService.getStats(id, id2).blockFirst().getId() == id);

    }

    @Test
    void create() {
        var id = UUID.randomUUID();

        when(webClientFacade.getAppearances(anyString())).thenReturn(
                Flux.just(LineupPlayer.builder()
                        .teamId(id).build())
        );

        when(webClientFacade.getMatch(anyString())).thenReturn(
                Mono.just(Match.builder()
                        .awayScore(1)
                        .homeScore(1)
                        .date(LocalDateTime.now())
                        .awayTeam(id)
                        .awayTeam(UUID.randomUUID()).build())
        );

        when(webClientFacade.getStats(anyString())).thenReturn(
                Flux.just(StatMetric.builder().build())
        );

        List<PlayerMatch> res = new ArrayList<>();
        playerMatchService.create(id, res::add);

        assertFalse(res.isEmpty());
    }

    @Test
    void next() {

        var id = UUID.randomUUID();

        when(webClientFacade.getAppearances(anyString())).thenReturn(
                Flux.just(LineupPlayer.builder()
                        .date(LocalDateTime.now().plusDays(1))
                        .teamId(id).build())
        );

        when(webClientFacade.getMatch(anyString())).thenReturn(
                Mono.just(Match.builder()
                        .awayScore(1)
                        .homeScore(1)
                        .date(LocalDateTime.now())
                        .awayTeam(id)
                        .awayTeam(UUID.randomUUID()).build())
        );

        when(webClientFacade.getStats(anyString())).thenReturn(
                Flux.just(StatMetric.builder().build())
        );

        List<PlayerMatch> res = new ArrayList<>();
        playerMatchService.next(UUID.randomUUID(), LocalDate.now(), res::add);

        assertFalse(res.isEmpty());
    }

}