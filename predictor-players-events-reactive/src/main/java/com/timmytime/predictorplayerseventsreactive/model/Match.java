package com.timmytime.predictorplayerseventsreactive.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Match {

    private UUID id;
    private LocalDateTime date;

    //replacement fields
    private UUID homeTeam;
    private UUID awayTeam;
    private Integer homeScore;
    private Integer awayScore;

}
