package com.timmytime.predictorplayerseventsreactive.enumerator;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum CountryCompetitions {
    ENGLAND(Arrays.asList("england_1", "england_2", "england_3", "england_4")),
    SCOTLAND(Arrays.asList("scotland_1", "scotland_2", "scotland_3", "scotland_4")),
    ITALY(Arrays.asList("italy_1", "italy_2")),
    FRANCE(Arrays.asList("france_1", "france_2")),
    SPAIN(Arrays.asList("spain_1", "spain_2")),
    GERMAN(Arrays.asList("german_1", "german_2")),
    HOLLAND(Arrays.asList("holland_1")),
    BELGIUM(Arrays.asList("belgium_1")),
    PORTUGAL(Arrays.asList("portugal_1")),
    GREECE(Arrays.asList("greece_1")),
    RUSSIA(Arrays.asList("russia_1")),
    TURKEY(Arrays.asList("turkey_1")),
    NORWAY(Arrays.asList("norway_1")),
    SWEDEN(Arrays.asList("sweden_1")),
    DENMARK(Arrays.asList("denmark_1"));

    List<String> competitions;

    CountryCompetitions(List<String> competitions) {
        this.competitions = competitions;
    }

    public static List<String> allCompetitions() {
        return Stream.of(CountryCompetitions.values())
                .map(CountryCompetitions::getCompetitions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
