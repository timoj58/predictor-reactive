package com.timmytime.predictorteamsreactive.service;

import com.timmytime.predictorteamsreactive.model.Team;

import java.util.List;

public interface TeamService {
    void loadTeams();
    List<Team> getTeams(String country);
}
