package com.timmytime.predictorscraperreactive.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Message {

    private String event;
    private String eventType;

    public Message(String competition) {
        this.eventType = competition.toUpperCase();
        this.event = "DATA_LOADED";
    }
}
