package com.timmytime.predictorplayerseventsreactive.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private UUID home;
    private UUID away;
    private String competition;
    private LocalDateTime date;
}
