package com.timmytime.predictorclientreactive.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class TeamAccuracy implements Serializable {

    private EventTypes type;
    private Integer total;
    private Integer success;
    private Double accuracy;

}
