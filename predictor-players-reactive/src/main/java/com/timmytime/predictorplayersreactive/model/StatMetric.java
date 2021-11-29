package com.timmytime.predictorplayersreactive.model;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatMetric {

    private UUID id;
    private LocalDateTime timestamp;
    private String label;
    private UUID player;
    private UUID team;
    private Integer value = 1;
    private Integer timeOfMetric;
    //new field
    private UUID matchId;

    public StatMetric(
            String label, Integer value
    ) {
        this.label = label;
        this.value = value;
    }

}
