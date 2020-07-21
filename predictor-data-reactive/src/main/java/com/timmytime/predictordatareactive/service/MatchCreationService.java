package com.timmytime.predictordatareactive.service;

import com.timmytime.predictordatareactive.model.ResultData;
import com.timmytime.predictordatareactive.model.Team;

import java.time.LocalDateTime;

public interface MatchCreationService {
    void create(
            Team homeTeam,
            Team awayTeam,
            LocalDateTime date,
            ResultData resultData
    );
}
