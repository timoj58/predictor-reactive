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
    private UUID opponent;
    private LocalDateTime date;
    private Boolean home;
    private List<StatMetric> stats;
}
