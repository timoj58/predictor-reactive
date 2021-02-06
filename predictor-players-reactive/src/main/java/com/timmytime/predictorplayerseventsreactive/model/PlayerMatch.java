package com.timmytime.predictorplayerseventsreactive.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document
public class PlayerMatch {

    @Id
    private UUID id;
    private UUID playerId;
    private UUID home;
    private LocalDate date;
    private UUID away;
    //and then we need the stats...
    private List<StatMetric> stats;
    //duration and conceded also required
    private Integer minutes;
    private Integer conceded;
}
