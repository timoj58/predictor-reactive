package com.timmytime.predictorteamsreactive.response;


import com.timmytime.predictorteamsreactive.model.Match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionEventOutcomeCsv {

    protected UUID home;
    protected UUID away;
    private String outcome;
    private Integer goals;

    public CompetitionEventOutcomeCsv(
            Match match
    ) {
        this.home = match.getHomeTeam();
        this.away = match.getAwayTeam();
        this.goals = match.getHomeScore() + match.getAwayScore();

        this.outcome = match.getAwayScore() == match.getHomeScore() ? "draw"
                : match.getHomeScore() > match.getAwayScore() ? "homeWin" : "awayWin";

    }


    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(home).append(",");
        stringBuilder.append(away).append(",");

        stringBuilder.append(outcome)
                .append(",").append(goals);

        return stringBuilder.toString();
    }
}
