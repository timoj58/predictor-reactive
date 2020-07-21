package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;
import org.json.JSONArray;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LineupPlayerService {
    void processPlayers(
            JSONArray players,
            Team team,
            UUID matchId,
            UUID lineupId,
            LocalDateTime date,
            ResultData resultData,
            String lineupType
    );
    Mono<Void> deleteByLineup(UUID lineupId);
}
