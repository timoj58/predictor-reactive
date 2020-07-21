package com.timmytime.predictoreventscraperreactive.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScraperResponse {

    private String document;
    private JsonNode response;
}
