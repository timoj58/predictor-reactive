package com.timmytime.predictormessagereactive.repo;

import com.timmytime.predictormessagereactive.model.FileRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRequestRepo extends ReactiveMongoRepository<FileRequest, String> {
}
