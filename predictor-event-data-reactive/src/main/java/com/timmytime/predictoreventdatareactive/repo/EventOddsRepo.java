package com.timmytime.predictoreventdatareactive.repo;


import com.timmytime.predictoreventdatareactive.model.EventOdds;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventOddsRepo extends ReactiveMongoRepository<EventOdds, UUID> {

    Mono<Void> deleteByProviderAndEventDateBefore(String provider, LocalDateTime date);

    Flux<EventOdds> findByCompetition(String competition);

    Mono<EventOdds> findByProviderAndEventAndEventDateAndPriceAndTeamsContains(String provider, String event, LocalDateTime eventDate, Double price, List<UUID> teams);

}
