package com.timmytime.predictorplayerseventsreactive.request;


import com.timmytime.predictorplayerseventsreactive.model.PlayerMatch;
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

    public PlayerEventOutcomeCsv(PlayerMatch playerMatch) {
        this.id = playerMatch.getPlayerId();
        this.player = playerMatch.getPlayerId();

        this.opponent = playerMatch.getOpponent();
        this.home = playerMatch.getHome() ? "home" : "away";

        playerMatch.getStats().forEach(this::setStats);
    }

    private void setStats(StatMetric stat) {

        switch (stat.getLabel()) {
            case "yellows":
                this.yellow += stat.getValue();
                break;
            case "goals":
                this.goals += stat.getValue();
                break;
            case "assists":
                this.assists += stat.getValue();
                break;
            default:
                break;
        }
    }

}
