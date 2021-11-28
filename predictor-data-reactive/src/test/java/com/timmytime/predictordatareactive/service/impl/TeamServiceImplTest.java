package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.TeamRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeamServiceImplTest {

    private static final TeamRepo teamRepo = mock(TeamRepo.class);
    private static final UUID teamToFind = UUID.randomUUID();
    TeamServiceImpl teamService
            = new TeamServiceImpl( teamRepo);

    @BeforeAll
    public static void setUp() {
        List<Team> teams = new ArrayList<>();

        Team test = new Team();
        test.setLabel("something united");
        test.setCountry("england");
        test.setCompetition("england_1");
        test.setId(teamToFind);

        teams.add(test);

        when(
                teamRepo.findAll()
        ).thenReturn(Flux.fromStream(teams.stream()));
    }

    @Test
    void matchTeams(){

        var home = UUID.randomUUID();
        var away = UUID.randomUUID();

        when(teamRepo.findByCompetitionAndLabel("england_1", "away"))
                .thenReturn(Mono.just(Team.builder()
                        .id(away)
                        .label("away").build()));

        when(teamRepo.findByCompetitionAndLabel("england_1", "home"))
                .thenReturn(Mono.just(Team.builder()
                        .id(home)
                        .label("home").build()));

        var result = teamService.getMatchTeams("england_1", "home", "away").block();

        assertTrue(result.getAway().get().getId().equals(away) &&
                result.getHome().get().getId().equals(home));

    }





}