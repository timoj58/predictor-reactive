package com.timmytime.predictorscraperreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Result extends ScraperModel {
    private Integer homeScore;
    private Integer awayScore;
    private String date;
    private String homeTeam;
    private String homeTeamEspnId;
    private String awayTeam;
    private String awayTeamEspnId;
    private String country;
    private String competition;

}
