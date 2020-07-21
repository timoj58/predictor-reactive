package com.timmytime.predictorscraperreactive.configuration;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteRules {

    private String id;
    private String url;
    private Boolean active;
    private Integer occurs;
    private String xpath;
    private List<String> ranges;
    private String index;
    private Integer order = 0;
}
