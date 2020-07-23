package com.timmytime.predictordatareactive.repo;


import com.timmytime.predictordatareactive.model.StatMetric;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StatMetricRepo extends ReactiveMongoRepository<StatMetric, UUID> {

    Flux<StatMetric> findByPlayer(UUID player);

    Flux<StatMetric> findByIdIn(List<UUID> ids);

    Flux<StatMetric> findByTeamNullAndPlayerNull();

    Mono<Void> deleteByMatchId(UUID matchId);

    Flux<StatMetric> findByPlayerNotNullAndTimestampAfter(LocalDateTime timestamp);

    Flux<StatMetric> findByPlayerAndMatchId(UUID player, UUID matchId);

}
