package com.timmytime.predictordatareactive.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Match {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private LocalDateTime date;

    //replacement fields
    @EqualsAndHashCode.Include
    private UUID homeTeam;
    @EqualsAndHashCode.Include
    private UUID awayTeam;
    private Integer homeScore;
    private Integer awayScore;

    public Match(UUID homeTeam, UUID awayTeam, LocalDateTime date) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
    }

}
