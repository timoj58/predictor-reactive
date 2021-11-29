package com.timmytime.predictorscraperreactive.configuration;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ResultsConfiguration {

    @Getter
    private final List<Pair<CompetitionFixtureCodes, String>> urls = new ArrayList<>();

    public ResultsConfiguration(
            @Value("${scraper.results}") String resultsUrl
    ) {
        Flux.fromArray(CompetitionFixtureCodes.values())
                .subscribe(code -> urls.add(
                        Pair.of(
                                code,
                                resultsUrl.replace("{league}", code.getCode()
                                )
                        )
                ));
    }

}
