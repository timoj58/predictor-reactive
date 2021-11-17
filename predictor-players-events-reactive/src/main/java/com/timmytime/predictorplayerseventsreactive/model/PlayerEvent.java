package com.timmytime.predictorplayerseventsreactive.model;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PlayerEvent implements Serializable {

    private FantasyEventTypes eventType;
    private String label;
    private Integer value = 0;

    public PlayerEvent(FantasyEventTypes eventType, Integer value) {
        this.eventType = eventType;
        this.label = eventType.name().toLowerCase();
        this.value = value;
    }

    public PlayerEvent(StatMetric statMetric) {

        switch (statMetric.getLabel()) {
            case "yellows":
                this.eventType = FantasyEventTypes.YELLOW_CARD;
                break;
            case "goals":
                this.eventType = FantasyEventTypes.GOALS;
                break;
            case "assists":
                this.eventType = FantasyEventTypes.ASSISTS;
                break;
            default:
                this.eventType = FantasyEventTypes.UNKNOWN;
                break;
        }

        this.label = statMetric.getLabel();
        this.value = statMetric.getValue();
    }


}
