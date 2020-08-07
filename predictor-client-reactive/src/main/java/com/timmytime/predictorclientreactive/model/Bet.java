package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Bet implements Serializable {

    private Double price;
    private BetProvider betProvider;
    private UUID betId;
    private String details;

    public Bet(UUID betId, BetProvider betProvider, Double price, String details) {
        this.betId = betId;
        this.betProvider = betProvider;
        this.price = price;
        this.details = details;
    }


}
