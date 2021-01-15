package com.timmytime.predictordatareactive.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountryConfig {
    private String country;
    private List<CompetitionConfig> competitions;
}
