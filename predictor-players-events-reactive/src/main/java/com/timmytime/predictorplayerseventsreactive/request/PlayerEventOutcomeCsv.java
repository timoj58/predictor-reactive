package com.timmytime.predictorplayerseventsreactive.request;


import com.timmytime.predictorplayerseventsreactive.model.StatMetric;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerEventOutcomeCsv {

    private UUID id;

    private UUID player;
    private UUID opponent;
    private String home;
    private Integer goals = 0;
    private Integer assists = 0;
    private Integer yellow = 0;


    public PlayerEventOutcomeCsv(UUID id, UUID player, UUID opponent, String home) {
        this.id = id;
        this.player = player;
        this.opponent = opponent;
        this.home = home;
    }

    private void setStats(StatMetric stat) {

        switch (stat.getLabel()) {
            case "Yellow Card":
                this.yellow += stat.getValue();
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
