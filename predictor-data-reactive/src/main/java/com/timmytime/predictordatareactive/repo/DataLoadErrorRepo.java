package com.timmytime.predictordatareactive.repo;


import com.timmytime.predictordatareactive.model.DataLoadError;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataLoadErrorRepo extends ReactiveMongoRepository<DataLoadError, UUID> {

}
