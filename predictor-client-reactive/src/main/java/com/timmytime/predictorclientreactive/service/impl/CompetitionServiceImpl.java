package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.Competition;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.facade.IS3Facade;
import com.timmytime.predictorclientreactive.model.CompetitionResponse;
import com.timmytime.predictorclientreactive.model.CountryAndCompetitionResponse;
import com.timmytime.predictorclientreactive.model.CountryResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Service("competitionService")
public class CompetitionServiceImpl implements ILoadService {

    private final IS3Facade s3Facade;
    private final ShutdownService shutdownService;


    @Override
    public void load() {

        List<CountryAndCompetitionResponse> countryAndCompetitionResponses = new ArrayList<>();

        Flux.fromStream(
                Stream.of(CountryCompetitions.values())
        ).doOnNext(country -> {

            log.info("processing {}", country.name());

            CountryAndCompetitionResponse countryAndCompetitionResponse = new CountryAndCompetitionResponse();
            countryAndCompetitionResponse.setCountryResponse(new CountryResponse(country.name().toLowerCase()));

            country.getCompetitions()
                    .stream()
                    .forEach(competition -> countryAndCompetitionResponse
                            .getCompetitionResponses()
                            .add(new CompetitionResponse(
                                    country.name(), Competition.valueOf(competition)
                            )));

            countryAndCompetitionResponses.add(countryAndCompetitionResponse);

        }).doFinally(finish -> {

            log.info("finishing");
            try {
                s3Facade.put("leagues", new ObjectMapper().writeValueAsString(countryAndCompetitionResponses));
                shutdownService.receive(CompetitionServiceImpl.class.getName());
            } catch (JsonProcessingException e) {
                log.error("json issue", e);
            }

        })
                .subscribe();

    }

}
