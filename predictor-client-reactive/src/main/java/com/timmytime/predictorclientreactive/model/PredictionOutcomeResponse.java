package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionOutcomeResponse {

    private String eventDate;
    private String home;
    private String away;

    private Boolean outcome;

    private String predictions;
    private String score;


    public PredictionOutcomeResponse(PredictionOutcomeCacheResponse predictionOutcomeCacheResponse) {
        this.eventDate = predictionOutcomeCacheResponse.getEventDate();
        this.home = predictionOutcomeCacheResponse.getHome();
        this.away = predictionOutcomeCacheResponse.getAway();
        this.outcome = predictionOutcomeCacheResponse.getOutcome();
        this.score = predictionOutcomeCacheResponse.getScore();
        this.predictions = predictionOutcomeCacheResponse.getPredictions();
    }

}
