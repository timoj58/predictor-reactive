package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class TeamPerformanceResponse {

    private UUID teamId;
    private String team;
    private List<TeamAccuracy> accuracy = new ArrayList<>();
    private List<PredictionOutcomeResponse> predictionOutcomes = new ArrayList<>();
    private List<MarketRating> marketRatings = new ArrayList<>();
    private String movement;

    public TeamPerformanceResponse() {

    }

    public TeamPerformanceResponse(TeamPerformanceCacheResponse teamPerformanceCacheResponse) {
        this.teamId = teamPerformanceCacheResponse.getTeamId();
        this.team = teamPerformanceCacheResponse.getTeam();
        this.accuracy = teamPerformanceCacheResponse.getAccuracy();
        this.marketRatings = teamPerformanceCacheResponse.getMarketRatings();
        this.movement = teamPerformanceCacheResponse.getMovement();

        this.predictionOutcomes =
                teamPerformanceCacheResponse
                        .getPredictionOutcomes()
                        .stream()
                        .map(PredictionOutcomeResponse::new)
                        .collect(Collectors.toList());

    }

}
