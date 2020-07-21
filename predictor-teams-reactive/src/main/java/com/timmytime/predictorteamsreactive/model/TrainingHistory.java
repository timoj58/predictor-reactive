package com.timmytime.predictorteamsreactive.model;

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
public class TrainingHistory {

    @Id
    private UUID id;
    LocalDateTime date;
    //important
    LocalDateTime fromDate;
    LocalDateTime toDate;
    private String country;
    private Boolean completed = Boolean.FALSE;

    public TrainingHistory(String country, LocalDateTime fromDate){
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.country = country;
        this.fromDate = fromDate;
    }

    public TrainingHistory(String country, LocalDateTime fromDate, LocalDateTime toDate){
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.country = country;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
