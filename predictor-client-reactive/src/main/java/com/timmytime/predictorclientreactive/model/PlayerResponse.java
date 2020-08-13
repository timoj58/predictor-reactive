package com.timmytime.predictorclientreactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String currentTeam;
    private UUID teamId;
    private Integer appearances;
    private Integer goals;
    private Integer assists;
    private Integer redCards;
    private Integer yellowCards;
    private Integer saves;


    private Double fantasyEventScore; //saves time.  also should use this for the mobile
    private String fantasyEventKey; //saves time.  also should use this for the mobile


    private List<FantasyResponse> fantasyResponse = new ArrayList<>();
    private List<FantasyEvent> averages = new ArrayList<>();

}
