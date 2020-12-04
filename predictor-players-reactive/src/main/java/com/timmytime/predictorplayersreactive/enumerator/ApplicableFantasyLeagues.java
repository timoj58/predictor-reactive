package com.timmytime.predictorplayersreactive.enumerator;

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

    public String getCountry() {
        return country;
    }

    private final String country;

    ApplicableFantasyLeagues(String country) {
        this.country = country;
    }

    public static List<ApplicableFantasyLeagues> findByCountry(String country) {
        return Arrays.asList(
                ApplicableFantasyLeagues.values()
        ).stream()
                .filter(f -> f.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    public static List<String> getCountries() {
        return Arrays.asList(
                ApplicableFantasyLeagues.values()
        ).stream()
                .map(m -> m.getCountry())
                .distinct()
                .collect(Collectors.toList());
    }
}
