package com.timmytime.predictormessagereactive.enumerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EventType {
    ALL, //predictions completed / start / stop / players training
    ENGLAND,
    ENGLAND_1,
    ENGLAND_2,
    ENGLAND_3,
    ENGLAND_4,
    SPAIN,
    SPAIN_1,
    SPAIN_2,
    PORTUGAL,
    PORTUGAL_1,
    DENMARK,
    DENMARK_1,
    HOLLAND,
    HOLLAND_1,
    BELGIUM,
    BELGIUM_1,
    GREECE,
    GREECE_1,
    TURKEY,
    TURKEY_1,
    SWEDEN,
    SWEDEN_1,
    NORWAY,
    NORWAY_1,
    FRANCE,
    FRANCE_1,
    FRANCE_2,
    RUSSIA,
    RUSSIA_1,
    GERMAN,
    GERMAN_1,
    GERMAN_2,
    ITALY,
    ITALY_1,
    ITALY_2,
    SCOTLAND,
    SCOTLAND_1,
    SCOTLAND_2,
    SCOTLAND_3,
    SCOTLAND_4;

    public static List<EventType> countries() {
        return Arrays.asList(EventType.values())
                .stream().filter(f -> !f.equals(EventType.ALL))
                .filter(f -> !f.name().contains("_"))
                .collect(Collectors.toList());
    }

    public static List<EventType> competitions() {
        return Arrays.asList(EventType.values())
                .stream().filter(f -> !f.equals(EventType.ALL))
                .filter(f -> f.name().contains("_"))
                .collect(Collectors.toList());
    }

    public static List<EventType> competitionsAndAll() {
        return Stream.concat(
                EventType.competitions().stream(),
                Stream.of(EventType.ALL)
        ).collect(Collectors.toList());
    }

    public static List<EventType> countriesAndCompetitions() {
        return Stream.concat(
                EventType.countries().stream(),
                EventType.competitions().stream()
        ).collect(Collectors.toList());
    }

}
