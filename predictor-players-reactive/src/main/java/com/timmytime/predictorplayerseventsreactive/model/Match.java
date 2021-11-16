package com.timmytime.predictorplayerseventsreactive.model;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    private UUID id;
    private LocalDateTime date;

    //replacement fields
    private UUID homeTeam;
    private UUID awayTeam;
    private Integer homeScore;
    private Integer awayScore;

}
