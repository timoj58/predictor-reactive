package com.timmytime.predictordatareactive.repo;

import com.timmytime.predictordatareactive.model.Result;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepo extends ReactiveMongoRepository<Result, Integer> {

}
