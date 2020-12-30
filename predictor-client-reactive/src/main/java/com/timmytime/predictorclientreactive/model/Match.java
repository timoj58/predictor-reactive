package com.timmytime.predictorclientreactive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {

    private LocalDateTime date;
    private UUID homeTeam;
    private UUID awayTeam;
    private Integer homeScore;
    private Integer awayScore;

}
