package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.LineupPlayer;
import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.repo.LineupPlayerRepo;
import com.timmytime.predictordatareactive.service.LineupPlayerService;
import com.timmytime.predictordatareactive.service.PlayerService;
import com.timmytime.predictordatareactive.service.StatMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            LocalDateTime date
    ) {

        log.info("creating lineup");

        playerService.process(players).forEach(player ->
                player.subscribe(
                        created -> {
                            lineupPlayerRepo.save(
                                    new LineupPlayer(created.getId(), matchId, team.getId(), date)
                            ).subscribe();


                            created.setLastAppearance(date.toLocalDate());
                            created.setLatestTeam(team.getId());

                                statMetricService.create(
                                        matchId,
                                        created,
                                        date
                                ).forEach(Mono::subscribe);

                            playerService.save(created).subscribe();

                        }
                ));
    }

    @Override
    public Mono<Void> deleteByMatch(UUID matchId) {
        return lineupPlayerRepo.deleteByMatchId(matchId);
    }

    @Override
    public Flux<LineupPlayer> find(
            UUID player,
            String date) {

        return date == null ?
                lineupPlayerRepo.findByPlayer(player) :
        lineupPlayerRepo.findByPlayerAndDateGreaterThanEqual(player, LocalDate.parse(date,
                DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
    }

    @Override
    public Mono<Long> totalAppearances(UUID player) {
        return lineupPlayerRepo.findByPlayer(player).count();
    }

}
