package com.timmytime.predictorplayersreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LineupPlayer implements Serializable {

    private UUID id;
    private UUID player;
    private Integer appearance;

    //new fields
    private UUID matchId;
    private UUID teamId;
    private LocalDateTime date;

}
