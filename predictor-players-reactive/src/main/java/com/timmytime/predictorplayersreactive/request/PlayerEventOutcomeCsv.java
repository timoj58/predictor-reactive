package com.timmytime.predictorplayersreactive.request;


import com.timmytime.predictorplayersreactive.model.PlayerMatch;
import com.timmytime.predictorplayersreactive.model.StatMetric;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerEventOutcomeCsv {

    private UUID id;

    private UUID player;
    private UUID opponent;
    private String home;
    private Integer minutes;
    private Integer conceded;
    private Integer goals = 0;
    private Integer assists = 0;
    private Integer saves = 0;
    private Integer red = 0;
    private Integer yellow = 0;


    public PlayerEventOutcomeCsv(UUID id, UUID player, UUID opponent, String home){
        this.id = id;
        this.player = player;
        this.opponent = opponent;
        this.home = home;
    }

    public PlayerEventOutcomeCsv(PlayerMatch playerMatch){
        this.id = playerMatch.getPlayerId();
        this.player = playerMatch.getPlayerId();

        this.opponent = playerMatch.getOpponent();
        this.home = playerMatch.getHome() ? "home" : "away";

        this.minutes = playerMatch.getMinutes();
        this.conceded = playerMatch.getConceded();

        playerMatch.getStats().stream().forEach(stat -> setStats(stat));
    }

    private void setStats(StatMetric stat) {

        switch (stat.getLabel()) {
            case "Red Card":
                this.red += stat.getValue();
                break;
            case "Yellow Card":
                this.yellow += stat.getValue();
                break;
            case "saves":
                this.saves += stat.getValue();
                break;
            case "goals":    //need to fix this.  double counting i think. due to old stats still present.
                this.goals += stat.getValue();
                break;
            case "Goal":
            case "Goal - Free-kick":
            case "Goal - Header":
            case "Penalty - Scored":
                break;
            case "Own Goal":
                break;
            case "totalShots":
                break;
            case "shotsOnTarget":
                break;
            case "foulsCommited":
                break;
            case "foulsSuffered":
                break;
            case "goalAssists":
                this.assists += stat.getValue();
                break;
            default:
                break;
        }
    }

}
