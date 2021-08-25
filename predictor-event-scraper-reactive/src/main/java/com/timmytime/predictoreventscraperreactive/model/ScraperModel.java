package com.timmytime.predictoreventscraperreactive.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScraperModel {
    private String provider;
    private String competition;
    private JsonNode data;
}
