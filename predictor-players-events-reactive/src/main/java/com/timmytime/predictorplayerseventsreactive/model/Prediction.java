package com.timmytime.predictorplayerseventsreactive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {


    private String key;
    private Double score;

    @Override
    public String toString() {
        return "{ \"key\": \"" + key + "\"," +
                " \"score\": \"" + score + "\"" + "}";
    }

}

