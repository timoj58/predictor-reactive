package com.timmytime.predictoreventscraperreactive.enumerator;

public enum ScraperTypeKeys {
    PADDYPOWER_ODDS("n/a"),
    LADBROKES_ODDS("n/a"),
    UNIBET_ODDS("n/a"),
    BETFRED_ODDS("n/a"),
    BET888_ODDS("n/a"),
    BETWAY_ODDS("n/a");

    private String key;

    ScraperTypeKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
