package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Match;
import com.timmytime.predictordatareactive.model.Result;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.ResultRepo;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.MatchService;
import com.timmytime.predictordatareactive.service.StatMetricService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class MatchCreationServiceImplTest {

    private static final MatchService matchService = mock(MatchService.class);
    private static final StatMetricService statMetricService = mock(StatMetricService.class);
    private static final LineupPlayerService lineupPlayerService = mock(LineupPlayerService.class);
    private static final ResultRepo resultRepo = mock(ResultRepo.class);

    private static ResultData resultData;
    private final MatchCreationServiceImpl matchCreationService
            = new MatchCreationServiceImpl(
            matchService,
            statMetricService,
            lineupPlayerService,
            resultRepo
    );

    @BeforeAll
    public static void setUp() throws IOException {
        FileSystemResource fileSystemResource = new FileSystemResource("./src/main/resources/match.json");
        FileSystemResource fileSystemResource2 = new FileSystemResource("./src/main/resources/result.json");
        FileSystemResource fileSystemResource3 = new FileSystemResource("./src/main/resources/lineup.json");

        Result result = new Result();
        result.setMatch(FileUtils.readFileToString(fileSystemResource.getFile()));
        result.setResult(FileUtils.readFileToString(fileSystemResource2.getFile()));
        result.setLineup(FileUtils.readFileToString(fileSystemResource3.getFile()));
        resultData = new ResultData(result);
        resultData.setId(1);

        when(matchService.save(any())).thenReturn(Mono.just(new Match()));

        when(resultRepo.findById(anyInt())).thenReturn(Mono.just(new Result()));
        when(resultRepo.save(any())).thenReturn(Mono.just(new Result()));

    }

    @Test
    public void createMatchTest() throws InterruptedException {

        Team home = new Team();
        home.setId(UUID.randomUUID());
        home.setLabel("Huddersfield Town");
        Team away = new Team();
        away.setId(UUID.randomUUID());
        away.setLabel("Luton Town");

        matchCreationService.create(
                home, away, LocalDateTime.now(), resultData
        );

        Thread.sleep(1000L);

        verify(matchService, atLeastOnce()).save(any());
        verify(resultRepo, atLeastOnce()).save(any());

    }

}