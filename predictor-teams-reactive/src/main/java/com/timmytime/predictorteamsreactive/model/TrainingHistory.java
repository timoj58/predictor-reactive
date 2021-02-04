package com.timmytime.predictorteamsreactive.model;

import com.timmytime.predictorteamsreactive.enumerator.Training;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingHistory {

    LocalDateTime date;
    //important
    LocalDateTime fromDate;
    LocalDateTime toDate;
    @Id
    private UUID id;
    private String country;
    private Boolean completed = Boolean.FALSE;
    private Training type = Training.TRAIN_RESULTS;

    public TrainingHistory(Training type, String country, LocalDateTime fromDate) {
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.country = country.toLowerCase();
        this.fromDate = fromDate;
        this.type = type;
    }

    public TrainingHistory(Training type, String country, LocalDateTime fromDate, LocalDateTime toDate) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.date = LocalDateTime.now();
        this.country = country.toLowerCase();
        this.fromDate = fromDate;
        this.toDate = toDate;

        if (this.toDate.isAfter(LocalDate.now().atStartOfDay())) {
            this.toDate = LocalDateTime.now();
        }
    }
}
