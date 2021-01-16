package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.configuration.CompetitionConfig;
import com.timmytime.predictordatareactive.configuration.CountryConfig;
import com.timmytime.predictordatareactive.configuration.DataConfig;
import com.timmytime.predictordatareactive.factory.SpecialCasesFactory;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.TeamRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TeamServiceImplTest {

    private final SpecialCasesFactory
            specialCasesFactory = new SpecialCasesFactory("./src/main/resources/config/");

    private static final TeamRepo teamRepo = mock(TeamRepo.class);

    private final DataConfig dataConfig = mock(DataConfig.class);


    TeamServiceImpl teamService
            = new TeamServiceImpl(dataConfig, teamRepo, specialCasesFactory);


    private static final UUID teamToFind = UUID.randomUUID();

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
    public void findByIdTest() throws InterruptedException {

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

        assertTrue(
                teamService.find(teamToFind, "england").getId().equals(teamToFind)
        );
    }

    @Test
    public void findByLabelTest() throws InterruptedException {

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

        assertTrue(
                teamService.getTeam("something u", "england").get().getId().equals(teamToFind)
        );

    }

    @Test
    public void loadNewTeamsTest() throws InterruptedException {

        CountryConfig england = new CountryConfig();
        CompetitionConfig competitionConfig = new CompetitionConfig();
        competitionConfig.setTeams("team1,team2,team3,");
        competitionConfig.setCompetition("england_1");
        england.setCountry("england");
        england.setCompetitions(Arrays.asList(competitionConfig));

        when(dataConfig.getCountries()).thenReturn(Arrays.asList(england));
        when(teamRepo.findByLabelIgnoreCase("team1")).thenReturn(Mono.empty());
        when(teamRepo.findByLabelIgnoreCase("team2")).thenReturn(Mono.just(Team.builder().id(UUID.randomUUID()).build()));
        when(teamRepo.findByLabelIgnoreCase("team3")).thenReturn(Mono.empty());
        when(teamRepo.save(any())).thenReturn(Mono.just(new Team()));

        teamService.loadNewTeams().subscribe();

        Thread.sleep(1000);

        verify(teamRepo, atMost(2)).save(any(Team.class));

    }

}