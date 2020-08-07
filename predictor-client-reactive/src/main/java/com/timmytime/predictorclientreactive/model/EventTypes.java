package com.timmytime.predictorclientreactive.model;

public enum EventTypes {

    DRAW("draw", Boolean.TRUE, "results"),
    HOME_WIN("homeWin", Boolean.TRUE, "results"),
    AWAY_WIN("awayWin", Boolean.TRUE, "results"),
    HOME_BEATS_AWAY("homeBeatsAway", Boolean.FALSE, "results"),
    AWAY_BEATS_HOME("awayBeatsHome", Boolean.FALSE, "results"),
    OVER_2_5("over2.5", Boolean.TRUE, "goals"),
    UNDER_2_5("under2.5", Boolean.TRUE, "goals"),
    OVER_1_5("over1.5", Boolean.TRUE, "goals"),
    UNDER_1_5("under1.5", Boolean.TRUE, "goals");

    private Boolean withMachine;
    private String label;
    private String market;

    EventTypes(String label, Boolean withMachine, String market) {
        this.withMachine = withMachine;
        this.label = label;
        this.market = market;
    }

    public Boolean getWithMachine() {
        return withMachine;
    }

    public void setWithMachine(Boolean withMachine) {
        this.withMachine = withMachine;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }


}
