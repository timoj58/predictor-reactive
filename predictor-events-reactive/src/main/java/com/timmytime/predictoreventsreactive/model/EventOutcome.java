package com.timmytime.predictoreventsreactive.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOutcome {

    @Id
    private UUID id;
    private UUID home;
    private UUID away;
    private String competition;
    private LocalDateTime date;
    private String prediction;
    private Boolean success; //was it correct on highest prediction
    private String eventType = "PREDICT_RESULTS";
    private Boolean previousEvent = Boolean.FALSE; //as in it was the last set of events per competition

    public String getCountry(){
        return competition.split("_")[0];
    }
}
