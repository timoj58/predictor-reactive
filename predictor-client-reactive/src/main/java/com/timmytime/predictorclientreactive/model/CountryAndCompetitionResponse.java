package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CountryAndCompetitionResponse implements Serializable {

    private CountryResponse countryResponse;
    private List<CompetitionResponse> competitionResponses = new ArrayList<>();
}
