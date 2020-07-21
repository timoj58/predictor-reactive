package com.timmytime.predictorplayersreactive.model;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
@Builder
public class FantasyOutcome {

    @Id
    private UUID id;
    private UUID playerId;
    private Boolean success;
    private FantasyEventTypes fantasyEventType;
    private String prediction;
    private String home;
    private UUID opponent;
    private LocalDateTime eventDate;

}
