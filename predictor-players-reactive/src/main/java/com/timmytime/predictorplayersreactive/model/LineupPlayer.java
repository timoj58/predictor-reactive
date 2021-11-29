package com.timmytime.predictorplayersreactive.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineupPlayer implements Serializable {

    private UUID id;
    private UUID player;
    private Integer appearance;

    //new fields
    private UUID matchId;
    private UUID teamId;
    private LocalDateTime date;

}
