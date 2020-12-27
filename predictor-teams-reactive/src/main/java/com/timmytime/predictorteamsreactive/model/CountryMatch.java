package com.timmytime.predictorteamsreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountryMatch {

    private String country;
    private Match match;

    public CountryMatch(
            String country,
            Match match
    ) {
        this.country = country;
        this.match = match;
    }
}
