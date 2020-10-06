package com.timmytime.predictorclientreactive.model;

import com.timmytime.predictorclientreactive.enumerator.Competition;
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
    private String label;
    private List<UpcomingEventResponse> upcomingEventResponses;

    public UpcomingCompetitionEventsResponse(Competition competition, List<UpcomingEventResponse> eventResponses) {
        this.competition = competition.name();
        this.label = competition.getLabel();
        this.upcomingEventResponses = eventResponses;
    }
}
