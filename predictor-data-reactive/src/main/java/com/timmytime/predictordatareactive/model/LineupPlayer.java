package com.timmytime.predictordatareactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "appearance", def = "{'player' : 1, 'date': 1}")
})
public class LineupPlayer implements Serializable {

    @Id
    private UUID id;
    private UUID player;

    //new fields
    private UUID matchId;
    private LocalDateTime date;
    private UUID teamId;


    public LineupPlayer(UUID player, UUID matchId, UUID teamId, LocalDateTime date) {
        this.id = UUID.randomUUID();
        this.player = player;
        this.matchId = matchId;
        this.teamId = teamId;
        this.date = date;
    }

}
