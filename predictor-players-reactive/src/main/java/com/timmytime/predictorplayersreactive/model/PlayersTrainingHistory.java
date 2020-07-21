package com.timmytime.predictorplayersreactive.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Getter
@Setter
@NoArgsConstructor
@Builder
public class PlayersTrainingHistory {

    @Id
    private UUID id;
    LocalDateTime date;
    //important
    LocalDateTime fromDate;
    LocalDateTime toDate;
    private Boolean completed = Boolean.FALSE;

    public PlayersTrainingHistory(LocalDateTime fromDate, LocalDateTime toDate){
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.fromDate = fromDate;
        this.toDate = toDate;
        if(this.toDate.isAfter(LocalDateTime.now())){
            this.toDate = LocalDateTime.now();
        }
    }

}
