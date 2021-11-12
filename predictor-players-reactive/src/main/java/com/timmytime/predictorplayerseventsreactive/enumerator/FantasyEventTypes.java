package com.timmytime.predictorplayerseventsreactive.enumerator;


//TODO slimming this all down.. only goals, assists, yellows cards now.
public enum FantasyEventTypes {
    GOALS(Boolean.TRUE, 0),
    ASSISTS(Boolean.TRUE, 1),
    YELLOW_CARD(Boolean.TRUE, 2),
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
