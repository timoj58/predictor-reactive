package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class SelectedBetResponse implements Serializable {

    private UUID id;
    private UUID batchId;
    private String home;
    private UUID homeId;
    private String away;
    private UUID awayId;
    private Double rating;
    private String predictions;

    private String eventDate;
    private String eventType;
    private Boolean win;
    private String market;
    private String event;
    private Double countryRating;
    private Double bestPrice;
    private String country;
    private String competition;

}
