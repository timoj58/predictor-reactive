package com.timmytime.predictordatareactive.response;

import com.timmytime.predictordatareactive.model.Team;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchTeams {
    private Optional<Team> home;
    private Optional<Team> away;
}
