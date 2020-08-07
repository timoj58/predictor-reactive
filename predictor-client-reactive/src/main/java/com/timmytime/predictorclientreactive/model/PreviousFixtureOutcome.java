package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PreviousFixtureOutcome implements Serializable {

    private String predictions;
    private String eventType;
    private Boolean success;
    private Integer totalGoals;

}
