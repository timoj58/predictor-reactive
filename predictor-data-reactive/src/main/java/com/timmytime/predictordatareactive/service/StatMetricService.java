package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.StatMetric;
import com.timmytime.predictordatareactive.model.Team;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StatMetricService {
    Mono<StatMetric> find(UUID id);
    void delete(UUID id);
    Mono<StatMetric> save(StatMetric match);
    List<Mono<StatMetric>> createTeamMetrics(
            UUID matchId,
            Team team,
            String label,
            LocalDateTime date,
            ResultData resultData
    );

    List<Mono<StatMetric>> createPlayerMatchEventMetrics(
            UUID matchId,
            Player player,
            ResultData resultData,
            LocalDateTime date
    );

    List<Mono<StatMetric>> createPlayerIndividualEventMetrics(
            UUID matchId,
            Player player,
            LocalDateTime date
    );

    Mono<Void> deleteByMatch(UUID matchId);

}
