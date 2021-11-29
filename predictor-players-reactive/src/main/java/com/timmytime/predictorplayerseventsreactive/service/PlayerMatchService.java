package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.LineupPlayer;
import com.timmytime.predictorplayerseventsreactive.model.Match;
import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
import com.timmytime.predictorplayerseventsreactive.model.StatMetric;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerMatchService {
    Flux<LineupPlayer> getAppearances(UUID player, Optional<LocalDate> date);

    Mono<Match> getMatch(UUID match);

    Flux<StatMetric> getStats(UUID match, UUID player);

    void create(UUID player, Consumer<PlayerMatch> consumer);

    void next(UUID player, LocalDate date, Consumer<PlayerMatch> consumer);
}
