package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.LineupPlayer;
import com.timmytime.predictorplayerseventsreactive.model.Match;
import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.model.StatMetric;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerMatchService {
    Flux<LineupPlayer> getAppearances(UUID player);

    Mono<Match> getMatch(UUID match);

    Flux<StatMetric> getStats(UUID match, UUID player);

    void create(UUID player, Consumer<PlayerMatch> consumer);
}
