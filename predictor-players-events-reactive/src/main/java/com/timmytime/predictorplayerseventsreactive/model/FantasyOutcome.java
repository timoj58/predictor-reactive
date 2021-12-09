package com.timmytime.predictorplayerseventsreactive.model;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
@Builder(toBuilder = true)
public class FantasyOutcome {
    @Id
    private UUID id;
    private UUID playerId;
    private FantasyEventTypes fantasyEventType;
    private String prediction;
    private String home;
    private UUID opponent;
    private LocalDateTime eventDate;
    private Boolean current = Boolean.FALSE;

}
