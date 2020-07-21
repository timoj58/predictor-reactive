package com.timmytime.predictordatareactive.repo;


import com.timmytime.predictordatareactive.model.TeamStats;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamStatsRepo extends ReactiveMongoRepository<TeamStats, UUID> {

    Flux<TeamStats> findByIdIn(List<UUID> ids);

}
