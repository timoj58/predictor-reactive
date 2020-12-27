package com.timmytime.predictordatareactive.factory;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.service.MatchCreationService;
import com.timmytime.predictordatareactive.service.MatchRepairService;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MatchFactoryTest {

    private final MatchCreationService matchCreationService = mock(MatchCreationService.class);
    private final MatchRepairService matchRepairService = mock(MatchRepairService.class);
    private final TeamService teamService = mock(TeamService.class);
    private final MatchService matchService = mock(MatchService.class);
    private final MatchFactory matchFactory
            = new MatchFactory(teamService, matchService, matchCreationService, matchRepairService);

    private static final Result result = new Result();

    @BeforeAll
    public static void setUp() throws IOException {
        //need to read in files as strings.
        FileSystemResource fileSystemResource = new FileSystemResource("./src/main/resources/match.json");
        FileSystemResource fileSystemResource2 = new FileSystemResource("./src/main/resources/result.json");
        FileSystemResource fileSystemResource3 = new FileSystemResource("./src/main/resources/lineup.json");

        result.setMatch(FileUtils.readFileToString(fileSystemResource.getFile()));
        result.setResult(FileUtils.readFileToString(fileSystemResource2.getFile()));
        result.setLineup(FileUtils.readFileToString(fileSystemResource3.getFile()));

    }

    @Test
    public void createMatchTest() throws InterruptedException {

        when(matchService.getMatch(any(), any(), any()))
                .thenReturn(Mono.empty());

        Team team = new Team();
        team.setId(UUID.randomUUID());

        when(teamService.getTeam(anyString(), anyString()))
                .thenReturn(Optional.of(team));

        matchFactory.createMatch(result);

        Thread.sleep(1000L);

        verify(matchCreationService, atLeastOnce()).create(
                any(),
                any(),
                any(),
                any()
        );

    }

    @Test
    public void repairMatchTest() throws InterruptedException {

        Match match = new Match();
        match.setId(UUID.randomUUID());

        when(matchService.getMatch(any(), any(), any()))
                .thenReturn(Mono.just(match));

        Team team = new Team();
        team.setId(UUID.randomUUID());

        when(teamService.getTeam(anyString(), anyString()))
                .thenReturn(Optional.of(team));

        matchFactory.createMatch(result);

        Thread.sleep(1000L);

        verify(matchRepairService, atLeastOnce()).repair(any());

    }

}