package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class MarketRating implements Serializable {

    private String market;
    private Double rating = 0.0;
    private Double withMachineRating = 0.0;
    private Double againsthMachineRating = 0.0;


}
