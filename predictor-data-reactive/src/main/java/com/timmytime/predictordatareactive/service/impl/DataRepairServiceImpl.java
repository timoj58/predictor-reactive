package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.repo.*;
import com.timmytime.predictordatareactive.service.DataRepairService;
import com.timmytime.predictordatareactive.service.LineupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;


@Service("dataRepairService")
public class DataRepairServiceImpl implements DataRepairService {

    private final Logger log = LoggerFactory.getLogger(DataRepairServiceImpl.class);

    private final LineupRepo lineupRepo;
    private final LineupPlayerRepo lineupPlayerRepo;
    private final MatchRepo matchRepo;
    private final TeamStatsRepo teamStatsRepo;
    private final StatMetricRepo statMetricRepo;

    @Autowired
    public DataRepairServiceImpl(
            LineupRepo lineupRepo,
            LineupPlayerRepo lineupPlayerRepo,
            MatchRepo matchRepo,
            TeamStatsRepo teamStatsRepo,
            StatMetricRepo statMetricRepo
    ){
        this.lineupRepo = lineupRepo;
        this.lineupPlayerRepo = lineupPlayerRepo;
        this.matchRepo = matchRepo;
        this.teamStatsRepo = teamStatsRepo;
        this.statMetricRepo = statMetricRepo;
    }

    @Override
    public void repairLineups() {

        log.info("repairing lineups");

        lineupRepo.findAll()
                .subscribe(
                        lineup -> {
                            //for each lineup
                            lineup.getPlayers()
                                    .stream()
                                    .forEach(player -> {
                                        player.setId(UUID.randomUUID());
                                        player.setLineupId(lineup.getId());
                                        lineupPlayerRepo.save(player).subscribe();
                                    });
                            lineup.getPlayingSubs()
                                    .stream()
                                    .forEach(player -> {
                                        player.setId(UUID.randomUUID());
                                        player.setLineupId(lineup.getId());
                                        lineupPlayerRepo.save(player).subscribe();
                                    });
                            lineup.getNonPlayingSubs()
                                    .stream()
                                    .forEach(player -> {
                                        player.setId(UUID.randomUUID());
                                        player.setLineupId(lineup.getId());
                                        lineupPlayerRepo.save(player).subscribe();
                                    });
                        }
                );

    }



    @Override
    public void repairMatches() {

        log.info("repairing matches and stats");

        matchRepo.findAll()
                .subscribe(match -> {
                    match.setHomeTeam(match.getTeams().get(0));
                    match.setAwayTeam(match.getTeams().get(1));

                    matchRepo.save(match).subscribe();

                            match.getStats().getTeamStats()
                .stream()
                .forEach(teamStat ->
                        teamStatsRepo.findById(teamStat)
                .subscribe(teamStats -> {
                /*    if(teamStats.getTeam().equals(match.getHomeTeam())){
                        match.setHomeScore(teamStats.getScore());
                    }else{
                        match.setAwayScore(teamStats.getScore());
                    }
                  */
                    lineupRepo.findById(teamStats.getLineup())
                            .subscribe(l -> {
                                l.setMatchId(match.getId());
                                lineupRepo.save(l).subscribe();
                            });

                    teamStats.getPlayerStatMetrics()
                            .stream()
                            .forEach(playerStat ->
                                    statMetricRepo.findById(playerStat)
                            .subscribe(statMetric -> {
                                statMetric.setMatchId(match.getId());
                                statMetricRepo.save(statMetric);
                            }));
                    teamStats.getTeamStatMetrics()
                            .stream()
                            .forEach(stat ->
                                    statMetricRepo.findById(stat)
                                            .subscribe(statMetric -> {
                                                statMetric.setMatchId(match.getId());
                                                statMetricRepo.save(statMetric);
                                            }));


                 matchRepo.save(match).subscribe();
                }));
                }
                );

    }
}
