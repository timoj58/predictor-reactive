package com.timmytime.predictordatareactive.service.impl;

import com.timmytime.predictordatareactive.model.Team;
import com.timmytime.predictordatareactive.model.TeamStats;
import com.timmytime.predictordatareactive.repo.TeamStatsRepo;
import com.timmytime.predictordatareactive.service.TeamService;
import com.timmytime.predictordatareactive.service.TeamStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service("scoreService")
public class TeamStatsServiceImpl implements TeamStatsService {

    private final TeamStatsRepo teamStatsRepo;

    @Autowired
    public TeamStatsServiceImpl(TeamStatsRepo teamStatsRepo) {
        this.teamStatsRepo = teamStatsRepo;
    }

    @Override
    public Mono<TeamStats> find(UUID id) {
        return teamStatsRepo.findById(id);
    }

    @Override
    public void delete(UUID id) {
        teamStatsRepo.deleteById(id).subscribe();
    }

    @Override
    public Mono<TeamStats> save(TeamStats teamStats) {
        return teamStatsRepo.save(teamStats);
    }
}
