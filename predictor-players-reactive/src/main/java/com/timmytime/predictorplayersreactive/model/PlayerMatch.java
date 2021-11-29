package com.timmytime.predictorplayersreactive.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@CompoundIndex(name = "unique_match_idx", def = "{'playerId' : 1, 'date' : 1}")
public class PlayerMatch {

    @Id
    private UUID id;
    private UUID playerId;
    private UUID opponent;
    private LocalDate date;
    private Boolean home;
    //and then we need the stats...
    private List<StatMetric> stats;
}
