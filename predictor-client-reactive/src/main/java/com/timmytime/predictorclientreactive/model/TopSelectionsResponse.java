package com.timmytime.predictorclientreactive.model;

import com.timmytime.predictorclientreactive.enumerator.FantasyEventTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TopSelectionsResponse {

    private Integer order;
    private String event;
    private List<PlayerResponse> playerResponses = new ArrayList<>();

    public TopSelectionsResponse(FantasyEventTypes fantasyEventTypes, List<PlayerResponse> playerResponses){
        this.event = fantasyEventTypes.name().toLowerCase();
        this.order = fantasyEventTypes.getOrder();
        this.playerResponses = playerResponses;
    }

    public void process(List<PlayerResponse> toProcess){

        this.playerResponses.addAll(toProcess);
        this.playerResponses =
                playerResponses.stream().sorted(Comparator.comparing(PlayerResponse::getFantasyEventScore).reversed())
                        .limit(event.equals(FantasyEventTypes.SAVES.name().toLowerCase()) ? 10 : 20).collect(Collectors.toList());

    }

}
