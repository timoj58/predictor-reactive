package com.timmytime.predictoreventdatareactive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MatchTeams {
    private Optional<Team> home;
    private Optional<Team> away;
}
