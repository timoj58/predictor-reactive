package com.timmytime.predictorclientreactive.service;

import com.timmytime.predictorclientreactive.model.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    void loadTeams();

    Team getTeam(String country, UUID id);

    List<Team> get(String country);

    Team getTeam(UUID id);

}
