package com.timmytime.predictorplayerseventsreactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @Transient
    private String label;

}
