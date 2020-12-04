package com.timmytime.predictoreventdatareactive.response;

import com.timmytime.predictoreventdatareactive.model.EventOdds;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Event {
    private UUID home;
    private UUID away;
    private String competition;
    private LocalDateTime date;

    public Event(EventOdds eventOdds) {
        this.home = eventOdds.getTeams().get(0);
        this.away = eventOdds.getTeams().get(1);

        this.competition = eventOdds.getCompetition();
        this.date = eventOdds.getEventDate();
    }
}
