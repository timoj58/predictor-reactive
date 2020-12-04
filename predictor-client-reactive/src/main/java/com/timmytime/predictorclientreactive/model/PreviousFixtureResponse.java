package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PreviousFixtureResponse implements Serializable {


    private Team home;
    private Team away;
    private String eventDate;

    private Integer homeScore;
    private Integer awayScore;

    private List<PreviousFixtureOutcome> previousFixtureOutcomes = new ArrayList<>();

    public PreviousFixtureResponse withScore(Match match) {

        this.setHomeScore(match.getHomeScore());
        this.setAwayScore(match.getAwayScore());

        return this;

    }

}
