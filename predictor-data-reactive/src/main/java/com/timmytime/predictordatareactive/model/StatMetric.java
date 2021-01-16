package com.timmytime.predictordatareactive.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "player_match_stat", def = "{'player' : 1, 'matchId': 1}")
})
public class StatMetric {

    @Id
    private UUID id;
    private LocalDateTime timestamp;
    private String label;
    private UUID player;
    private UUID team;
    private Integer value = 1;
    private Integer timeOfMetric;
    //new field
    private UUID matchId;

}
