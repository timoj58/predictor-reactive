package com.timmytime.predictorclientreactive.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class EventOutcome {

    private UUID id;
    private UUID home;
    private UUID away;
    private String competition;
    private LocalDateTime date = LocalDateTime.now();
    private String prediction;
    private Boolean success; //was it correct on highest prediction
    private String eventType = "PREDICT_RESULTS";
    private Boolean previousEvent = Boolean.FALSE; //as in it was the last set of events per competition
    private String country;

}
