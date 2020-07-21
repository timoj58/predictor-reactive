package com.timmytime.predictoreventscraperreactive.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookmakerSiteRules {


    private String id;
    private String url;
    private String connectionType = "web";
    private List<String> socketPool;
    private Boolean active;
    private String type;
    private String payload;
    private List<String> keys;
    private String extractConfig;
    private Integer order = 0;


}
