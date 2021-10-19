package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.*;
import com.timmytime.predictordatareactive.repo.LineupPlayerRepo;
import com.timmytime.predictordatareactive.repo.PlayerRepo;
import com.timmytime.predictordatareactive.repo.StatMetricRepo;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.StatMetricService;
import com.timmytime.predictordatareactive.service.TeamService;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LineupPlayerServiceImplTest {

    private static final PlayerRepo playerRepo = mock(PlayerRepo.class);
    private static final StatMetricRepo statMetricRepo = mock(StatMetricRepo.class);
    private static final LineupPlayerRepo lineupService = mock(LineupPlayerRepo.class);
    private static Team team;
    private static UUID teamStatsId = UUID.randomUUID();
    private static ResultData resultData;
    private final PlayerService playerService =
            new PlayerServiceImpl(mock(TeamService.class), playerRepo);
    private final StatMetricService statMetricService =
            new StatMetricServiceImpl(statMetricRepo);
    private final LineupPlayerServiceImpl lineupPlayerService
            = new LineupPlayerServiceImpl(
            playerService,
            lineupService,
            statMetricService
    );

    @BeforeAll
    public static void setUp() throws IOException {
        team = new Team();
        team.setId(UUID.randomUUID());

        FileSystemResource fileSystemResource2 = new FileSystemResource("./src/main/resources/result.json");
        FileSystemResource fileSystemResource3 = new FileSystemResource("./src/main/resources/lineup.json");

        Result result = new Result();
        result.setResult(FileUtils.readFileToString(fileSystemResource2.getFile()));
        result.setLineup(FileUtils.readFileToString(fileSystemResource3.getFile()));
        resultData = new ResultData(result);

        Player player = new Player();
        player.setId(UUID.randomUUID());
        player.setLabel("test");

        StatMetric statMetric = new StatMetric();
        statMetric.setId(UUID.randomUUID());

        when(playerRepo.findByLabel(anyString())).thenReturn(Mono.just(player));
        when(playerRepo.save(any(Player.class))).thenReturn(Mono.just(player));
        when(lineupService.save(any())).thenReturn(Mono.just(new LineupPlayer()));

        when(statMetricRepo.save(any(StatMetric.class))).thenReturn(Mono.just(statMetric));

        List<Player> placeholders = new ArrayList<>();

        IntStream.range(0, 100).forEach(i -> placeholders.add(Player.builder().label("TBC").id(UUID.randomUUID()).build()));

        when(playerRepo.findAll()).thenReturn(Flux.fromStream(placeholders.stream()));

        when(statMetricRepo.findByPlayer(any())).thenReturn(Flux.empty());

    }

    @Test
    public void createPlayersAndStatsTests() throws JSONException {

        lineupPlayerService.processPlayers(
                resultData.getLineups().getJSONObject("data").getJSONArray("home"),
                team,
                teamStatsId,
                LocalDateTime.now()
        );

        verify(playerRepo, atLeastOnce()).save(any(Player.class));
    }

}