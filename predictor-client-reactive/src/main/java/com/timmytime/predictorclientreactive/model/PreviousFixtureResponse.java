package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PreviousFixtureResponse implements Serializable {


    private Team home;
    private Team away;

    private Date eventDate;

    private Integer homeScore;
    private Integer awayScore;

    private List<PreviousFixtureOutcome> previousFixtureOutcomes = new ArrayList<>();

}
