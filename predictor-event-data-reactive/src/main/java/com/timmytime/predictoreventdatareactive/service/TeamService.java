package com.timmytime.predictoreventdatareactive.service;

import com.timmytime.predictoreventdatareactive.model.Team;

import java.util.Optional;

public interface TeamService {
    Optional<Team> find(String label, String competition);

    void init();
}
