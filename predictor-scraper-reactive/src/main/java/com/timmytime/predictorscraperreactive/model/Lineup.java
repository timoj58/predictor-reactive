package com.timmytime.predictorscraperreactive.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Lineup extends ScraperModel {

    private JsonNode data;
}
