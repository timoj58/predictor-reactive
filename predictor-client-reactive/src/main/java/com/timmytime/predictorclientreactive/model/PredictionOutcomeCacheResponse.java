package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PredictionOutcomeCacheResponse implements Serializable {

    private String eventDate;
    private String home;
    private String away;

    private Boolean outcome;
    //need to do this only on the way out..not for the cache.
    private String predictions;

    private String score;


}
