package com.timmytime.predictorplayersreactive.request;


import com.timmytime.predictorplayersreactive.model.PlayerMatch;
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
    private Integer minutes;
    private Integer conceded;
    private Integer goals;
    private Integer assists;
    private Integer saves;
    private Integer red;
    private Integer yellow;


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
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(player).append(",");

        stringBuilder
                .append(opponent)
                .append(",")
                .append(home)
                .append(",")
                .append(minutes)
                .append(",")
                .append(saves)
                .append(",")
                .append(conceded)
                .append(",")
                .append(goals)
                .append(",")
                .append(assists)
                .append(",")
                .append(red)
                .append(",")
                .append(yellow);

        return stringBuilder.toString();
    }


    //cant use mapper due to it calling toString (which is for CSV)
    public String getJson() {
        return "{\"opponent\": \"" + opponent + "\"," +
                "\"home\": \"" + home + "\"," +
                "\"player\": \"" + player + "\"," +
                "\"minutes\": \"" + minutes + "\"," +
                "\"saves\": \"" + saves + "\"," +
                "\"goals\": \"" + goals + "\"," +
                "\"assists\": \"" + assists + "\"," +
                "\"red\": \"" + red + "\"," +
                "\"yellow\": \"" + yellow + "\"," +
                "\"conceded\": \"" + conceded + "\" }";
    }


}
