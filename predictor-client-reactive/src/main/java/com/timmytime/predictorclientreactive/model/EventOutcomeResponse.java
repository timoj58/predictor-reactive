package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class EventOutcomeResponse implements Serializable {

    private String eventType;

    private Team home;
    private Team away;

    private String eventDate;
    private String predictions;

    public EventOutcomeResponse(EventOutcome eventOutcome) {
        ///the fucking mess from before.  this is to get mobile working again...
        this.predictions = eventOutcome.getPrediction();
        this.eventDate = eventOutcome.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.eventType = eventOutcome.getEventType();
    }

}
