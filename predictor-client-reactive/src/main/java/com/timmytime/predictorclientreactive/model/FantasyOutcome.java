package com.timmytime.predictorclientreactive.model;

import com.timmytime.predictorclientreactive.enumerator.FantasyEventTypes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FantasyOutcome {
    private UUID id;
    private UUID playerId;
    private FantasyEventTypes fantasyEventType;
    private String prediction;
    private String home;
    private UUID opponent;
    private LocalDateTime eventDate;
    private Boolean current = Boolean.TRUE;

}
