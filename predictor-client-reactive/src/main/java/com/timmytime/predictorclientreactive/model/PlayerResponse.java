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
public class PlayerResponse implements Serializable {

    private String label;
    private UUID id;
    private UUID currentTeam;
    private UUID teamId;

    private Double fantasyEventScore; //saves time.  also should use this for the mobile
    private String fantasyEventKey; //saves time.  also should use this for the mobile

    private List<FantasyResponse> fantasyResponse = new ArrayList<>();

    public PlayerResponse(PlayerResponse playerResponse, FantasyEvent fantasyEvent) {
        this.label = playerResponse.getLabel();
        this.id = playerResponse.getId();
        this.currentTeam = playerResponse.getCurrentTeam();
        this.fantasyEventScore = fantasyEvent.getFantasyEventScore();
        this.fantasyEventKey = fantasyEvent.getFantasyEventKey();
        this.fantasyResponse = playerResponse.getFantasyResponse();
    }
}
