package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.repo.PlayerRepo;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PlayerServiceImplTest {

    private final TeamService teamService = mock(TeamService.class);
    private final PlayerRepo playerRepo = mock(PlayerRepo.class);


    private final PlayerService playerService
            = new PlayerServiceImpl(teamService, playerRepo);


    @Test
    void initTest(){

        when(playerRepo.findAll()).thenReturn(Flux.empty());
        when(playerRepo.save(any())).thenReturn(Mono.just(Player.builder().build()));

        playerService.init();

        verify(playerRepo, atLeast(10000)).save(any());
    }

    @Test
    void createLineupNewPlayer() throws JSONException {

        when(playerRepo.findByEspnId(anyString())).thenReturn(
                Mono.empty()
        );

        var players = new JSONArray();

        players.put(new JSONObject().put("name", "tim").put("espnId", "tim1"));

        var res = playerService.process(players);

        res.forEach(r -> r.subscribe(
                p -> assertThat(p.getEspnId()).isEqualTo("tim1")
        ));
    }

    @Test
    void createLineupExistingPlayer() throws JSONException {

        var id = UUID.randomUUID();
        when(playerRepo.findByEspnId(anyString())).thenReturn(
                Mono.just(Player.builder()
                        .id(id).build())
        );

        var players = new JSONArray();

        players.put(new JSONObject().put("name", "tim").put("espnId", "tim1"));

        var res = playerService.process(players);

        res.forEach(r -> r.subscribe(
                p -> assertThat(p.getId()).isEqualTo(id)
        ));
    }

}