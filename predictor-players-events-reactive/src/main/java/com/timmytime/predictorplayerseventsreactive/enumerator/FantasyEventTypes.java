package com.timmytime.predictorplayerseventsreactive.enumerator;

public enum FantasyEventTypes {
    GOALS(Boolean.TRUE, 0),
    ASSISTS(Boolean.TRUE, 1),
    YELLOW_CARD(Boolean.TRUE, 2),
    UNKNOWN(Boolean.FALSE, -1);

    private final Boolean predict;
    private final Integer order;

    FantasyEventTypes(Boolean predict, Integer order) {
        this.predict = predict;
        this.order = order;
    }

    public Boolean getPredict() {
        return predict;
    }

}
