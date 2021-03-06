package com.timmytime.predictorclientreactive.enumerator;

public enum FantasyEventTypes {
    MINUTES(Boolean.FALSE, -1), //some data has incorrect range numbers...from scraper
    GOALS(Boolean.TRUE, 0),
    GOAL_TYPE(Boolean.FALSE, -1),
    OWN_GOALS(Boolean.FALSE, -1),
    SHOTS(Boolean.FALSE, -1),
    ON_TARGET(Boolean.FALSE, -1),
    FOULS_COMMITED(Boolean.FALSE, -1),
    FOULS_RECEIVED(Boolean.FALSE, -1),
    ASSISTS(Boolean.TRUE, 1),
    PENALTY_SAVED(Boolean.FALSE, -1),
    PENALTY_MISSED(Boolean.FALSE, -1),
    RED_CARD(Boolean.FALSE, -1),
    YELLOW_CARD(Boolean.TRUE, 2),
    GOALS_CONCEDED(Boolean.FALSE, -1),
    SAVES(Boolean.FALSE, 3),
    UNKNOWN(Boolean.FALSE, -1);

    /*
      reviewed ESPN its possible to get ASSISTS and PEN's

      need to fix all the data retrospectively for this now...hmmm.

      leave it for the top 5 leagues only -> and only for these stats.

     */

    private final Boolean predict;
    private final Integer order;

    FantasyEventTypes(Boolean predict, Integer order) {
        this.predict = predict;
        this.order = order;
    }

    public Boolean getPredict() {
        return predict;
    }

    public Integer getOrder() {
        return order;
    }


}
