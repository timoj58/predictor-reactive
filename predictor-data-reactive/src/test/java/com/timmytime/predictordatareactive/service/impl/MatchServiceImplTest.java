package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.MatchRepo;
import com.timmytime.predictordatareactive.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class MatchServiceImplTest {


    @InjectMocks
    private MatchServiceImpl matchService;

    @Mock
    private MatchRepo matchRepo;
    @Mock
    private TeamService teamService;

    @BeforeEach
    void init(){
        MockitoAnnotations.initMocks(this);

        when(teamService.getTeams(anyString())).thenReturn(Arrays.asList(
                Team.builder().id(UUID.randomUUID()).label("home").build(),
                Team.builder().id(UUID.randomUUID()).label("away").build()
        ));

        var matchId = UUID.randomUUID();

        when(matchRepo.findByHomeTeamInAndDateBetween(any(), any(), any())).thenReturn(
                Flux.just(Match.builder().id(matchId).build())
        );

        when(matchRepo.findByAwayTeamInAndDateBetween(any(), any(), any())).thenReturn(
                Flux.just(Match.builder().id(matchId).build())
        );
    }



    @Test
    void duplicateMatchesFilteredTest(){

        matchService.getMatchesByCountry("england", "01-01-2010", "01-01-2011").collectList()
                .subscribe(matches -> assertThat(matches.size()).isEqualTo(1));

    }

}