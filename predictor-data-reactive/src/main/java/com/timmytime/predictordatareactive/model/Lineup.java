package com.timmytime.predictordatareactive.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Lineup {

    @Id
    private UUID id;
    private UUID team;
    private LocalDateTime eventDate;
    //new fields
    private UUID matchId;

    //remove these...all are players.  time is indicator of if they played or not.
    private List<LineupPlayer> players = new ArrayList<>();
    private List<LineupPlayer> nonPlayingSubs = new ArrayList<>();
    private List<LineupPlayer> playingSubs = new ArrayList<>();

}
