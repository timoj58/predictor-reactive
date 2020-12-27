package com.timmytime.predictordatareactive.enumerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayerStats {
    GOALS("goals"),
    SAVES("saves"),
    SHOTS_ON_TARGET("shotsOnTarget"),
    TOTAL_SHOTS("totalShots"),
    FOULS_COMMITED("foulsCommited"),
    FOULS_SUFFERED("foulsSuffered"),
    GOAL_ASSISTS("goalAssists");

    private String key;

    PlayerStats(String key) {
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static List<String> getKeys() {
        return Arrays.asList(values()).stream()
                .map(m -> m.getKey())
                .collect(Collectors.toList());
    }


}
