package com.timmytime.predictorplayerseventsreactive.repo;

import com.timmytime.predictorplayerseventsreactive.model.PlayerResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerResponseRepo extends ReactiveMongoRepository<PlayerResponse, UUID> {
}
