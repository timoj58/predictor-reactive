package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Lineup;
import com.timmytime.predictordatareactive.repo.LineupRepo;
import com.timmytime.predictordatareactive.service.LineupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("lineupService")
public class LineupServiceImpl implements LineupService {

    private final LineupRepo lineupRepo;

    @Autowired
    public LineupServiceImpl(LineupRepo lineupRepo) {
        this.lineupRepo = lineupRepo;
    }

    @Override
    public Mono<Lineup> find(UUID id) {
        return lineupRepo.findById(id);
    }

    @Override
    public void delete(UUID id) {
        lineupRepo.deleteById(id).subscribe();
    }

    @Override
    public Mono<Lineup> save(Lineup lineup) {
        return lineupRepo.save(lineup);
    }

    @Override
    public Mono<Void> deleteByMatch(UUID matchId) {
        return lineupRepo.deleteByMatchId(matchId);
    }

    @Override
    public Flux<Lineup> findByMatch(UUID matchId) {
        return lineupRepo.findByMatchId(matchId);
    }
}
