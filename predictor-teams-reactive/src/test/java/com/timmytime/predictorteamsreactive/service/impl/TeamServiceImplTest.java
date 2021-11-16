package com.timmytime.predictorteamsreactive.service.impl;

import com.timmytime.predictorteamsreactive.facade.WebClientFacade;
import com.timmytime.predictorteamsreactive.model.Team;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TeamServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);

    private final TeamServiceImpl teamService
            = new TeamServiceImpl("data", webClientFacade);


    @Test
    public void loadTeams() {
        var team = new Team();
        team.setCountry("country");
        when(webClientFacade.getTeams(anyString()))
                .thenReturn(Flux.just(team));
        teamService.loadTeams();

        var teams = teamService.getTeams("england");
        assertTrue(teams.size() == 1);
    }


}