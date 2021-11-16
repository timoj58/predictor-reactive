package com.timmytime.predictorscraperreactive.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Message {

    private String country;
    private String competition;

    public Message(String competition) {
        this.country = competition.split("_")[0];
        this.competition = competition;
    }
}
