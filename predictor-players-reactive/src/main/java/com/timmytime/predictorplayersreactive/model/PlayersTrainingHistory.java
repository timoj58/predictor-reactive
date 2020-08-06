package com.timmytime.predictorplayersreactive.model;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayersTrainingHistory {

    @Id
    private UUID id;
    LocalDateTime date;
    //important
    LocalDateTime fromDate;
    LocalDateTime toDate;
    private Boolean completed = Boolean.FALSE;
    private FantasyEventTypes type;

    public PlayersTrainingHistory(FantasyEventTypes type, LocalDateTime fromDate, LocalDateTime toDate){
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.type = type;
        this.fromDate = fromDate;
        this.toDate = toDate;
        if(this.toDate.isAfter(LocalDateTime.now())){
            this.toDate = LocalDateTime.now();
        }
    }

    public PlayersTrainingHistory(FantasyEventTypes type, UUID id, LocalDateTime fromDate, LocalDateTime toDate){
        this.id = id;
        this.date = LocalDateTime.now();
        this.type = type;
        this.fromDate = fromDate;
        this.toDate = toDate;
        if(this.toDate.isAfter(LocalDateTime.now())){
            this.toDate = LocalDateTime.now();
        }
    }

}
