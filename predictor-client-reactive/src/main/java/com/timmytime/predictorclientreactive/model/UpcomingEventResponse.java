package com.timmytime.predictorclientreactive.model;


import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpcomingEventResponse{

    private Team home;
    private Team away;
    private String eventDate;
    private String country;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpcomingEventResponse that = (UpcomingEventResponse) o;
        return Objects.equals(home, that.home) &&
                Objects.equals(away, that.away);
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, away);
    }


}
