package com.timmytime.predictordatareactive.model;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Stats {
    //to remove completely
    private List<UUID> teamStats = new ArrayList<>();

    public Stats(List<UUID> teamStats){
        this.teamStats = teamStats;
    }

}
