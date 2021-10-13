package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.Player;
import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.StatMetric;
import com.timmytime.predictordatareactive.model.Team;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StatMetricService {

    Mono<StatMetric> save(StatMetric match);

    List<Mono<StatMetric>> create(
            UUID matchId,
            Player player,
            LocalDateTime date
    );

    Mono<Void> deleteByMatch(UUID matchId);

    Flux<StatMetric> find(@PathVariable UUID player, @PathVariable UUID match);

    Flux<StatMetric> getPlayerStats(@PathVariable UUID player);


}
