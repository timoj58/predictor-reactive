package com.timmytime.predictorplayerseventsreactive.enumerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ApplicableFantasyLeagues {
    ENGLAND_1("england"),
    ENGLAND_2("england"),
    SPAIN_1("spain"),
    ITALY_1("italy"),
    GERMAN_1("german"),
    FRANCE_1("france"),
    PORTUGAL_1("portugal");

    private final String country;

    ApplicableFantasyLeagues(String country) {
        this.country = country;
    }

    public static List<ApplicableFantasyLeagues> findByCountry(String country) {
        return Arrays.stream(
                ApplicableFantasyLeagues.values()
        )
                .filter(f -> f.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    public String getCountry() {
        return country;
    }
}
