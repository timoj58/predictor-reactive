package com.timmytime.predictorplayersreactive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerMatch {

    private UUID playerId;
    private UUID opponent;
    private LocalDateTime date;
    private Boolean home;
    //and then we need the stats...
}
