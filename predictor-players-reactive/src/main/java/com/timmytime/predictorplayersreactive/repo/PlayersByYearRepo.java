package com.timmytime.predictorplayersreactive.repo;


import com.timmytime.predictorplayersreactive.model.PlayersByYear;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PlayersByYearRepo extends ReactiveMongoRepository<PlayersByYear, Integer> {
}
