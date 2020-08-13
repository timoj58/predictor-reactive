package com.timmytime.predictorplayersreactive.repo;

import com.timmytime.predictorplayersreactive.model.PlayerResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerResponseRepo extends ReactiveMongoRepository<PlayerResponse, UUID> {
}
