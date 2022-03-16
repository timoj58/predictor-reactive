package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.service.InitService;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.ResultService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class InitServiceImplTest {

    private final TeamService teamService = mock(TeamService.class);
    private final PlayerService playerService = mock(PlayerService.class);
    private final ResultService resultService = mock(ResultService.class);

    private final InitService initService
            = new InitServiceImpl(teamService, playerService, resultService);

    @Test
    void initTest() throws InterruptedException {

        initService.init();

        Thread.sleep(100);

        verify(teamService, atLeastOnce()).init();
        verify(playerService,atLeastOnce()).init();
        verify(resultService, atLeastOnce()).init();
    }
}