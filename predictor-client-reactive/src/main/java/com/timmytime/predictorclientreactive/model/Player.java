package com.timmytime.predictorclientreactive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private UUID id;
    private String label;
    private UUID latestTeam;
    private LocalDate lastAppearance;
}
