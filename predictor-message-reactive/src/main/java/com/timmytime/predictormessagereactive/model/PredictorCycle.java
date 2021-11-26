package com.timmytime.predictormessagereactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PredictorCycle {

    @Id
    private final UUID id;
    private final LocalDateTime date;
    private final List<CycleEvent> cycleEvents;
    private final List<ActionEvent> actionEvents;
}
