package com.timmytime.predictordatareactive.repo;

import com.timmytime.predictordatareactive.model.Result;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ResultRepo extends ReactiveMongoRepository<Result, Integer> {

    Flux<Result> findByProcessed(Boolean processed);
}
