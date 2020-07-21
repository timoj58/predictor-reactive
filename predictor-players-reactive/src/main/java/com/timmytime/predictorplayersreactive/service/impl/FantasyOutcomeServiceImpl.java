package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import com.timmytime.predictorplayersreactive.repo.FantasyOutcomeRepo;
import com.timmytime.predictorplayersreactive.service.FantasyOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("fantasyOutcomeService")
public class FantasyOutcomeServiceImpl implements FantasyOutcomeService {

    private final FantasyOutcomeRepo fantasyOutcomeRepo;

    @Autowired
    public FantasyOutcomeServiceImpl(
            FantasyOutcomeRepo fantasyOutcomeRepo
    ){
        this.fantasyOutcomeRepo = fantasyOutcomeRepo;
    }

    @Override
    public Mono<FantasyOutcome> save(FantasyOutcome fantasyOutcome) {
        return fantasyOutcomeRepo.save(fantasyOutcome);
    }

    @Override
    public Mono<FantasyOutcome> find(UUID id) {
        return fantasyOutcomeRepo.findById(id);
    }
}
