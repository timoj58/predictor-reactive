package com.timmytime.predictordatareactive.model;

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
@Document
@NoArgsConstructor
public class LineupPlayer implements Serializable {

    @Id
    private UUID id;
    private UUID player;
    private Integer appearance;

    //new fields
    private UUID matchId;
    private LocalDateTime date;
    private UUID teamId;


    public LineupPlayer(UUID player, int appearance, UUID matchId, UUID teamId, LocalDateTime date) {
        this.id = UUID.randomUUID();
        this.player = player;
        this.appearance = appearance;
        this.matchId = matchId;
        this.teamId = teamId;
        this.date = date;
    }

}
