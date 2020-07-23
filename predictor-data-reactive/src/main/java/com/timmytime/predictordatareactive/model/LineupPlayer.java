package com.timmytime.predictordatareactive.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
