package com.timmytime.predictorteamsreactive.service;


import com.timmytime.predictorteamsreactive.model.CountryMatch;
import com.timmytime.predictorteamsreactive.response.CompetitionEventOutcomeCsv;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface TensorflowDataService {
    void load(CountryMatch match);
    List<CompetitionEventOutcomeCsv> getCountryCsv(
            @PathVariable String country,
            @PathVariable String fromDate,
            @PathVariable String toDate);
    void clear(String country);
}
