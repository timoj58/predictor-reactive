package com.timmytime.predictorteamsreactive.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventOutcome {

    private UUID id;
    private UUID home;
    private UUID away;
    private String competition;
    private LocalDateTime date;
    private String prediction;
    private Boolean success; //was it correct on highest prediction
    private String eventType = "PREDICT_RESULTS";
    private Boolean previousEvent = Boolean.FALSE;
}
