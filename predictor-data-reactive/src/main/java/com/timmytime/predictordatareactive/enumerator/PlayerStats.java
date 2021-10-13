package com.timmytime.predictordatareactive.enumerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayerStats {
    GOALS("goals"),
    YELLOW("yellows"),
    GOAL_ASSISTS("assists");

    private String key;

    PlayerStats(String key) {
        this.key = key;
    }

    public static List<String> getKeys() {
        return Arrays.stream(values())
                .map(PlayerStats::getKey)
                .collect(Collectors.toList());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
