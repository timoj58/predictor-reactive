package com.timmytime.predictordatareactive.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
public class TeamStats {

    @Id
    private UUID id;
    private UUID team;

    //remove these
    private Boolean homeTeam = Boolean.FALSE;
    private List<UUID> playerStatMetrics = new ArrayList<>();
    private List<UUID> teamStatMetrics = new ArrayList<>();
    private UUID lineup;


    private Integer score;

    //new fields
    private UUID matchId;

}
