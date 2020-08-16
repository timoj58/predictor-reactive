package com.timmytime.predictorplayersreactive.service;

import com.timmytime.predictorplayersreactive.model.Team;

import java.util.UUID;

public interface TeamService {
    void loadTeams();
    Team getTeam(UUID id);
}
