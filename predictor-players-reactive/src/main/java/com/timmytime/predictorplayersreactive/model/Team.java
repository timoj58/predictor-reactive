package com.timmytime.predictorplayersreactive.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Team implements Serializable {

    private UUID id;
    private String label;
    private String country;
    private String competition;

}
