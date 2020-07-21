package com.timmytime.predictorscraperreactive.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ScraperModel {
    protected Integer matchId;
    protected String type;

}
