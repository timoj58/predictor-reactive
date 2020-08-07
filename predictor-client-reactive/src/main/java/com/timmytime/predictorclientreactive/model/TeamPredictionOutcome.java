package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TeamPredictionOutcome implements Serializable {

    private UUID teamId;
    private String team;
    private List<TeamAccuracy> accuracy = new ArrayList<>();
    private List<PredictionOutcome> predictionOutcomes = new ArrayList<>();
    private List<MarketRating> marketRatings = new ArrayList<>();

}
