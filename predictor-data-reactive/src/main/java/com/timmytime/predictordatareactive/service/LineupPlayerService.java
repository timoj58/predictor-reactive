package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.LineupPlayer;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LineupPlayerService {
    void processPlayers(
            JSONArray players,
            Team team,
            UUID matchId,
            LocalDateTime date,
            ResultData resultData,
            String lineupType
    );

    Mono<Void> deleteByMatch(UUID matchId);

    Flux<LineupPlayer> find(
            @PathVariable UUID player);

    Mono<Long> totalAppearances(
            @PathVariable UUID player
    );
}
