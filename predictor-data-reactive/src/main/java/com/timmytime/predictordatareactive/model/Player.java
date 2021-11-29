package com.timmytime.predictordatareactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
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
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Player {

    @Transient
    @JsonIgnore
    private final List<JSONObject> stats = new ArrayList<>();
    @Id
    private UUID id;
    private String label;
    private UUID latestTeam;
    private LocalDate lastAppearance;
    private String espnId;
}
