package com.timmytime.predictorplayerseventsreactive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private UUID id;
    private String label;
    private UUID latestTeam;
    private LocalDate lastAppearance;
    //WOULD SAVE TIME: 30 minutes approx... private Boolean isGoalkeeper = Boolean.TRUE; //default
}
