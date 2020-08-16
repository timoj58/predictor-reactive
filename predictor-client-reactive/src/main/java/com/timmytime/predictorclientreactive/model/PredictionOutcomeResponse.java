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


}
