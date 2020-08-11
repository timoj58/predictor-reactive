package com.timmytime.predictordatareactive.model;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Match  {

    @Id
    private UUID id;
    private LocalDateTime date;

    //replacement fields
    private UUID homeTeam;
    private UUID awayTeam;
    private Integer homeScore;
    private Integer awayScore;

    public Match(UUID homeTeam, UUID awayTeam, LocalDateTime date){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
    }

}
