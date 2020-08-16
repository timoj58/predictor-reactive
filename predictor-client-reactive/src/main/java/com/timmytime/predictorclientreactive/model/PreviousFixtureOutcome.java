package com.timmytime.predictorclientreactive.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviousFixtureOutcome implements Serializable {

    private String predictions;
    private String eventType;
    private Boolean success;
    private Integer totalGoals;

}
