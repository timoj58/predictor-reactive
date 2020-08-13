package com.timmytime.predictorclientreactive.enumerator;

public enum Competition {

    england_1("Premier League", "English", Boolean.TRUE),
    england_2("Championship", "English",Boolean.TRUE),
    england_3("League One", "English",Boolean.FALSE),
    england_4("League Two", "English",Boolean.FALSE),
    england_5("National League", "English",Boolean.FALSE),
    italy_1("Serie A", "Italian",Boolean.TRUE),
    italy_2("Serie B", "Italian",Boolean.FALSE),
    spain_1("La Liga", "Spanish",Boolean.TRUE),
    spain_2("La Liga 2", "Spanish",Boolean.FALSE),
    france_1("Ligue 1", "French",Boolean.TRUE),
    france_2("Ligue 2", "French",Boolean.FALSE),
    german_1("Bundesliga", "German",Boolean.TRUE),
    german_2("2. Bundesliga", "German",Boolean.FALSE),
    scotland_1("Premiership", "Scottish",Boolean.FALSE),
    scotland_2("Championship", "Scottish",Boolean.FALSE),
    scotland_3("League One", "Scottish",Boolean.FALSE),
    scotland_4("League Two", "Scottish",Boolean.FALSE),
    greece_1("Superleague", "Greek",Boolean.FALSE),
    turkey_1("Süper Lig", "Turkish",Boolean.FALSE),
    norway_1("Eliteserien", "Norwegian",Boolean.FALSE),
    russia_1("Чемпионат России по футболу", "Russian",Boolean.FALSE),
    sweden_1("Allsvenskan", "Swedish",Boolean.FALSE),
    denmark_1("1st Division", "Danish",Boolean.FALSE),
    belgium_1("First Division A", "Belgium",Boolean.FALSE),
    holland_1("Eredivisie", "Dutch",Boolean.FALSE),
    portugal_1("Primeira Liga", "Portuguese",Boolean.TRUE);

    private String label;
    private String country;
    private Boolean fantasyLeague;

    Competition(String label, String country, Boolean fantasyLeague) {
        this.label = label;
        this.country = country;
        this.fantasyLeague = fantasyLeague;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getFantasyLeague() {
        return fantasyLeague;
    }

    public void setFantasyLeague(Boolean fantasyLeague) {
        this.fantasyLeague = fantasyLeague;
    }



}
