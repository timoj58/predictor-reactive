package com.timmytime.predictoreventscraperreactive.configuration;

import com.timmytime.predictoreventscraperreactive.enumerator.CompetitionFixtureCodes;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Configuration
@ConfigurationProperties(prefix = "fixtures")
public class FixturesScraperConfiguration {

    @Setter
    private String url;
    @Getter
    private List<CompetitionFixtures> competitionFixtures = new ArrayList<>();

    @PostConstruct
    private void init() {

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
