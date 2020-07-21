package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.repo.EventOutcomeRepo;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("eventOutcomeService")
public class EventOutcomeServiceImpl implements EventOutcomeService {

    private final EventOutcomeRepo eventOutcomeRepo;

    @Autowired
    public EventOutcomeServiceImpl(
            EventOutcomeRepo eventOutcomeRepo
    ){
        this.eventOutcomeRepo = eventOutcomeRepo;
    }

    @Override
    public Mono<EventOutcome> save(EventOutcome eventOutcome) {
        return eventOutcomeRepo.save(eventOutcome);
    }

    @Override
    public Mono<EventOutcome> find(UUID id) {
        return eventOutcomeRepo.findById(id);
    }
}
