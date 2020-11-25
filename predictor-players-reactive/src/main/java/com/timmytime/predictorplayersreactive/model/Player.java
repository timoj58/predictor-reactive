package com.timmytime.predictorplayersreactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private UUID id;
    private String label;
    private UUID latestTeam;
    private LocalDate lastAppearance;
    //WOULD SAVE TIME: 30 minutes approx... private Boolean isGoalkeeper = Boolean.TRUE; //default
}
