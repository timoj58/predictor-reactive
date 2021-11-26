package com.timmytime.predictoreventscraperreactive.configuration;

import com.timmytime.predictoreventscraperreactive.enumerator.CompetitionFixtureCodes;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Configuration
public class FixturesScraperConfiguration {

    @Getter
    private final List<CompetitionFixtures> competitionFixtures = new ArrayList<>();

    public FixturesScraperConfiguration(
            @Value("${fixtures.url}") String url
    ) {
        LocalDate now = LocalDate.now();

        Stream.of(CompetitionFixtureCodes.values())
                .forEach(competition ->
                        competitionFixtures.add(
                                CompetitionFixtures.builder()
                                        .code(competition)
                                        .url(url.replace("{competition}", competition.getCode())
                                                .replace("{date}", now.format(
                                                        DateTimeFormatter.ofPattern("yyyyMMdd")
                                                )))
                                        .build()
                        ));
    }

}
