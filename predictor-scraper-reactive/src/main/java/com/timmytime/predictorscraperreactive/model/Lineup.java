package com.timmytime.predictorscraperreactive.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Getter
@Setter
@NoArgsConstructor
public class Lineup extends ScraperModel {

    private JsonNode data;
}
