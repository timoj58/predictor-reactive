package com.timmytime.predictordatareactive.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
public class StatMetric {

    @Id
    private UUID id;
    private LocalDateTime timestamp;
    private String label;
    private UUID player;
    private UUID team;
    private Integer value = 1;
    private Integer timeOfMetric;
    //new field
    private UUID matchId;

}
