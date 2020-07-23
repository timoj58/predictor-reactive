package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PlayerMatchService {
    Flux<LineupPlayer> getAppearances(UUID player, String fromDate, String toDate);
    Mono<Match> getMatch(UUID match);
    Flux<StatMetric> getStats(UUID match, UUID player);
    void create(UUID player, LocalDateTime fromDate, LocalDateTime toDate);
}
