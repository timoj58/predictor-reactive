package com.timmytime.predictoreventscraperreactive.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScraperModel {
    private String provider;
    private String competition;
    private JsonNode data;
}
