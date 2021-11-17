package com.timmytime.predictoreventsreactive.request;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {
    private UUID id;
    private UUID home;
    private UUID away;

    public Prediction(
            EventOutcome eventOutcome
    ) {
        this.id = eventOutcome.getId();
        this.home = eventOutcome.getHome();
        this.away = eventOutcome.getAway();
    }
}
