package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpcomingCompetitionEventsResponse implements Serializable {

    private String competition;
    private List<UpcomingEventResponse> upcomingEventResponses;

    public UpcomingCompetitionEventsResponse(String competition, List<UpcomingEventResponse> eventResponses) {
        this.competition = competition;
        this.upcomingEventResponses = eventResponses;
    }
}
