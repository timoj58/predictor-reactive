package com.timmytime.predictorplayerseventsreactive.service.impl;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.model.Player;
import com.timmytime.predictorplayerseventsreactive.model.Team;
import com.timmytime.predictorplayerseventsreactive.repo.PlayerResponseRepo;
import com.timmytime.predictorplayerseventsreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerMatchService;
import com.timmytime.predictorplayerseventsreactive.service.PlayerService;
import com.timmytime.predictorplayerseventsreactive.service.TeamService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PlayerResponseServiceImplTest {

    private final PlayerService playerService = mock(PlayerService.class);
    private final FantasyOutcomeService fantasyOutcomeService = mock(FantasyOutcomeService.class);
    private final TeamService teamService = mock(TeamService.class);
    private final PlayerMatchService playerMatchService = mock(PlayerMatchService.class);
    private final PlayerResponseRepo playerResponseRepo = mock(PlayerResponseRepo.class);


    private final PlayerResponseServiceImpl playerResponseService
            = new PlayerResponseServiceImpl(
            0,
            playerService,
            fantasyOutcomeService,
            teamService,
            playerMatchService,
            playerResponseRepo
    );

    @Test
    @Disabled
    public void loadPlayerTest() throws InterruptedException {


        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);
        player.setLabel("test player");
        player.setLatestTeam(UUID.randomUUID());

        Team team = new Team();
        team.setLabel("test team");

        FantasyOutcome fantasyOutcome = new FantasyOutcome();
        fantasyOutcome.setCurrent(Boolean.TRUE);
        fantasyOutcome.setFantasyEventType(FantasyEventTypes.ASSISTS);

        FantasyOutcome fantasyOutcome1 = new FantasyOutcome();
        fantasyOutcome1.setFantasyEventType(FantasyEventTypes.GOALS);

        //need some predictions to add in.  dont have data at present.
        when(playerService.get(playerId)).thenReturn(player);
        when(teamService.getTeam(player.getLatestTeam())).thenReturn(team);
        when(fantasyOutcomeService.findByPlayer(playerId)).thenReturn(
                Flux.fromStream(
                        Arrays.asList(fantasyOutcome, fantasyOutcome1).stream()
                )
        );

        Arrays.asList(
                FantasyEventTypes.values()
        ).stream()
                .filter(f -> f.getPredict() == Boolean.TRUE)
                .forEach(fantasyEventTypes -> {
                    FantasyOutcome add = new FantasyOutcome();
                    add.setPlayerId(playerId);
                    add.setFantasyEventType(fantasyEventTypes);
                    playerResponseService.addResult(add);
                });


        Thread.sleep(2000L);

        verify(playerResponseRepo, atLeastOnce()).save(any());

    }

}