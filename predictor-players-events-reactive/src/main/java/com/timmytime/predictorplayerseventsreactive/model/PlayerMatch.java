package com.timmytime.predictorplayerseventsreactive.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerMatch {

    private UUID playerId;
    private UUID home;
    private LocalDateTime date;
    private UUID away;
    //and then we need the stats...
    private List<StatMetric> stats;
    //duration and conceded also required
    private Integer minutes;
    private Integer conceded;
}
