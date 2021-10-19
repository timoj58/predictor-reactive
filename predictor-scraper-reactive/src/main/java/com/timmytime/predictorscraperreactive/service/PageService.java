package com.timmytime.predictorscraperreactive.service;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import org.apache.commons.lang3.tuple.Triple;

public interface PageService {
    void addPageRequest(Triple<CompetitionFixtureCodes, ScraperType, String> request);
}
