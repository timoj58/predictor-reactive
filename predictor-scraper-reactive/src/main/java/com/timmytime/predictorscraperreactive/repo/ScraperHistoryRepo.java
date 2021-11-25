package com.timmytime.predictorscraperreactive.repo;

import com.timmytime.predictorscraperreactive.model.ScraperHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScraperHistoryRepo extends MongoRepository<ScraperHistory, UUID> {
    Optional<ScraperHistory> findFirstByOrderByDateDesc();

}
