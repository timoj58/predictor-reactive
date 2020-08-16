package com.timmytime.predictorplayersreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
public class PlayerResponse implements Serializable {

    private String label;
    @Id
    private UUID id;
    private String currentTeam;
    private Integer appearances;
    private Integer goals;
    private Integer assists;
    private Integer redCards;
    private Integer yellowCards;
    private Integer saves;

    //this is for recent form...
    private Double hardmanRed;
    private Double hardmanYellow;
    private Double marksman;
    private Double wizard;

    private Double fantasyEventScore; //saves time.  also should use this for the mobile
    private String fantasyEventKey; //saves time.  also should use this for the mobile

    private List<FantasyResponse> fantasyResponse = new ArrayList<>();

    private List<FantasyEvent> averages = new ArrayList<>();

}
