package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatchSelectionResponse {

    private Integer order;
    private String event;
    private List<PlayerResponse> playerResponses = new ArrayList<>();

}
