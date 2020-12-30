package com.timmytime.predictorclientreactive.model;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team implements Serializable {

    private UUID id;
    private String label;
    private String country;
    private String competition;

}
