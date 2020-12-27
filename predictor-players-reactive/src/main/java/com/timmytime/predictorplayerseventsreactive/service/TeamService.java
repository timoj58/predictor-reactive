package com.timmytime.predictorplayerseventsreactive.service;

import com.timmytime.predictorplayerseventsreactive.model.Team;

import java.util.UUID;

public interface TeamService {
    void loadTeams();

    Team getTeam(UUID id);
}
