package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.LineupPlayer;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.LineupPlayerRepo;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.StatMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service("lineupPlayerService")
public class LineupPlayerServiceImpl implements LineupPlayerService {

    private final PlayerService playerService;
    private final LineupPlayerRepo lineupPlayerRepo;
    private final StatMetricService statMetricService;


    @Override
    public void processPlayers(
            JSONArray players,
            Team team,
            UUID matchId,
            LocalDateTime date,
            ResultData resultData,
            String lineupType
    ) {

        playerService.process(players).forEach(homePlayers ->
                homePlayers.subscribe(
                        homePlayer -> { //wrong now...
                            lineupPlayerRepo.save(
                                    new LineupPlayer(homePlayer.getId(), homePlayer.getDuration(), matchId, team.getId(), date)
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
                                ).forEach(Mono::subscribe);

                                statMetricService.createPlayerIndividualEventMetrics(
                                        matchId,
                                        homePlayer,
                                        date
                                ).forEach(Mono::subscribe);

                                //save player...
                                playerService.save(homePlayer).subscribe(
                                        playerService::addFantasyFootballer
                                );
                            }
                        }
                ));
    }

    @Override
    public Mono<Void> deleteByMatch(UUID matchId) {
        return lineupPlayerRepo.deleteByMatchId(matchId);
    }

    @Override
    public Flux<LineupPlayer> find(
            UUID player) {

        return lineupPlayerRepo.findByPlayer(
                player);
    }

    @Override
    public Mono<Long> totalAppearances(UUID player) {
        return lineupPlayerRepo.findByPlayer(player).count();
    }

}
