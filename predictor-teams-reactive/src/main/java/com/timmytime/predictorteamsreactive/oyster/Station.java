package com.timmytime.predictorteamsreactive.oyster;


import lombok.Getter;

import java.util.List;

public enum Station {
    HOLBORN(List.of(1)),
    EARLS_COURT(List.of(1, 2)),
    WIMBLEDON(List.of(3)),
    HAMMERSMITH(List.of(2)),
    EPPING(List.of(7)),
    BUS_STOP(List.of(0));

    @Getter
    private List<Integer> zones;

    Station(List<Integer> zones){
        this.zones = zones;
    }
}
