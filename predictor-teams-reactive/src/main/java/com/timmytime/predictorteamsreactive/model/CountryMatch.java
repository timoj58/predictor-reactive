package com.timmytime.predictorteamsreactive.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryMatch {

    private String country;
    private Match match;

}
