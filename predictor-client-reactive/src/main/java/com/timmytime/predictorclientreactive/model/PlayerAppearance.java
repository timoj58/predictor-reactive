package com.timmytime.predictorclientreactive.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerAppearance implements Serializable {

    private UUID matchId;
    private Date date;
    private Boolean home;
    private UUID playerTeam;
    private UUID opponent;
    private String homeTeam;
    private String awayTeam;


    private List<PlayerEvent> statMetrics = new ArrayList<>();
    private Integer duration;

    public PlayerAppearance(){

    }

}

