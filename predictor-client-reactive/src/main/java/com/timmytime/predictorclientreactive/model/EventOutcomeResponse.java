package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EventOutcomeResponse implements Serializable {


    private String eventType;

    private Team home;
    private Team away;

    private Date eventDate;

    private String predictions;

    private List<Bet> bets;

    private TeamPredictionOutcome homeOutcomes;
    private TeamPredictionOutcome awayOutcomes;
    private Double rating;
    private Integer successCount;
    private UUID eventOutcome;

}
