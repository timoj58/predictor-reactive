package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
