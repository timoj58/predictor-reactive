package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FantasyResponse implements Serializable {

    Map<Integer, Double> goals = new HashMap<>();
    Map<Integer, Double> assists = new HashMap<>();
    Map<Integer, Double> redCards = new HashMap<>();
    Map<Integer, Double> yellowCards = new HashMap<>();
    private UUID opponent;
    private Boolean isHome;
    private Double minutes;
    private Double conceded;
    private Double saves;


}
