package com.timmytime.predictoreventscraperreactive.enumerator;

import lombok.Getter;

@Getter
public enum CompetitionFixtureCodes {
    ENGLAND_1("eng.1"),
    ENGLAND_2("eng.2"),
    ENGLAND_3("eng.3"),
    ENGLAND_4("eng.4"),
    SCOTLAND_1("sco.1"),
    SCOTLAND_2("sco.2"),
    SCOTLAND_3("sco.3"),
    SCOTLAND_4("sco.4"),
    FRANCE_1("fra.1"),
    FRANCE_2("fra.2"),
    SPAIN_1("esp.1"),
    SPAIN_2("esp.2"),
    GERMAN_1("ger.1"),
    GERMAN_2("ger.2"),
    ITALY_1("ita.1"),
    ITALY_2("ita.2"),
    PORTUGAL_1("por.1"),
    TURKEY_1("tur.1"),
    GREECE_1("gre.1"),
    DENMARK_1("den.1"),
    NORWAY_1("nor.1"),
    SWEDEN_1("swe.1"),
    RUSSIA_1("rus.1"),
    BELGIUM_1("bel.1"),
    HOLLAND_1("ned.1");

    private String code;

    CompetitionFixtureCodes(String code){
        this.code = code;
    }
}
