package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionResponse implements Serializable {

    private String competition;
    private String country;
    private String label;
    private Boolean fantasyLeague;

}
