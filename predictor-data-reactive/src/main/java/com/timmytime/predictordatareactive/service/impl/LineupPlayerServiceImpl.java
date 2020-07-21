package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.LineupPlayer;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.LineupPlayerRepo;
import com.timmytime.predictordatareactive.repo.LineupRepo;
import com.timmytime.predictordatareactive.service.*;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service("lineupPlayerService")
public class LineupPlayerServiceImpl implements LineupPlayerService {

    private final Logger log = LoggerFactory.getLogger(LineupPlayerServiceImpl.class);

    private final PlayerService playerService;
    private final LineupPlayerRepo lineupPlayerRepo;
    private final StatMetricService statMetricService;
    private final TeamStatsService teamStatsService;

    @Autowired
    public LineupPlayerServiceImpl(
            PlayerService playerService,
            LineupPlayerRepo lineupPlayerRepo,
            StatMetricService statMetricService,
            TeamStatsService teamStatsService
    ){
        this.playerService = playerService;
        this.lineupPlayerRepo = lineupPlayerRepo;
        this.statMetricService = statMetricService;
        this.teamStatsService = teamStatsService;
    }

    @Override
    public void processPlayers(
            JSONArray players,
            Team team,
            UUID matchId,
            UUID lineupId,
            LocalDateTime date,
            ResultData resultData,
            String lineupType
    ){

        playerService.process(players).stream()
                .forEach(homePlayers ->
                        homePlayers.subscribe(
                                homePlayer -> { //wrong now...
                                    lineupPlayerRepo.save(
                                            new LineupPlayer(homePlayer.getId(), homePlayer.getDuration(), lineupId)
                                    ).subscribe();


                                    homePlayer.setLastAppearance(date.toLocalDate());
                                    homePlayer.setLatestTeam(team.getId());
                                    //create player stats...
                                    if (Arrays.asList("players", "subs").contains(lineupType)) {
                                        statMetricService.createPlayerMatchEventMetrics(
                                                matchId,
                                                homePlayer,
                                                resultData,
                                                date
                                        ).stream()
                                                .forEach(stat ->
                                                        stat.subscribe());

                                        statMetricService.createPlayerIndividualEventMetrics(
                                                matchId,
                                                homePlayer,
                                                date
                                        ).stream()
                                                .forEach(stat ->
                                                        stat.subscribe());

                                        //save player...
                                        playerService.save(homePlayer).subscribe();
                                    }
                                }
                        ));
    }

    @Override
    public Mono<Void> deleteByLineup(UUID lineupId) {
        return deleteByLineup(lineupId);
    }
}
