package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.factory.SpecialCasesFactory;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.TeamRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TeamServiceImplTest {

    private final SpecialCasesFactory
            specialCasesFactory = new SpecialCasesFactory("./src/main/resources/config/");

    private static final TeamRepo teamRepo = mock(TeamRepo.class);


    TeamServiceImpl teamService
            = new TeamServiceImpl(teamRepo, specialCasesFactory);


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


}