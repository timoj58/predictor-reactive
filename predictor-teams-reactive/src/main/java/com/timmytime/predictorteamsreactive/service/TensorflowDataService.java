package com.timmytime.predictorteamsreactive.service;


import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.model.Match;
import com.timmytime.predictorteamsreactive.response.CompetitionEventOutcomeCsv;

import java.util.List;

public interface TensorflowDataService {
    void load(CountryMatch match);
    List<CompetitionEventOutcomeCsv> getCountryCsv(String country, String fromDate, String toDate);
}
