package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.model.FantasyOutcome;
import com.timmytime.predictorplayersreactive.repo.FantasyOutcomeRepo;
import com.timmytime.predictorplayersreactive.service.FantasyOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("fantasyOutcomeService")
public class FantasyOutcomeServiceImpl implements FantasyOutcomeService {

    private final FantasyOutcomeRepo fantasyOutcomeRepo;

    @Autowired
    public FantasyOutcomeServiceImpl(
            FantasyOutcomeRepo fantasyOutcomeRepo
    ) {
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

    @Override
    public Flux<FantasyOutcome> findByPlayer(UUID id) {
        return fantasyOutcomeRepo.findByPlayerId(id);
    }

    @Override
    public Flux<FantasyOutcome> toFix() {
        return fantasyOutcomeRepo.findByPredictionNull();
    }

    //TODO @PostConstruct
    private void init() {
        //no longer validating for now, so simply turn them all off when rebooting system.
        //but not until its live.  need data for now ;)
        fantasyOutcomeRepo.findByCurrent(Boolean.TRUE)
                .subscribe(outcome -> {
                    outcome.setCurrent(Boolean.FALSE);
                    fantasyOutcomeRepo.save(outcome).subscribe();
                });
    }


}
