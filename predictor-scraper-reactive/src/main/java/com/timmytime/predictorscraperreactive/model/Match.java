package com.timmytime.predictorscraperreactive.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Match extends ScraperModel {

    private List<JsonNode> data = new ArrayList<>();

}
