package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.facade.IS3Facade;
import com.timmytime.predictorclientreactive.model.CompetitionResponse;
import com.timmytime.predictorclientreactive.model.CountryAndCompetitionResponse;
import com.timmytime.predictorclientreactive.model.CountryResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.util.Competition;
import com.timmytime.predictorclientreactive.util.CountryCompetitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("competitionService")
public class CompetitionServiceImpl implements ILoadService {

    private final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    private final IS3Facade s3Facade;
    private final ShutdownService shutdownService;

    @Autowired
    public CompetitionServiceImpl(
            IS3Facade s3Facade,
            ShutdownService shutdownService
    ){
        this.s3Facade = s3Facade;
        this.shutdownService = shutdownService;

    }

    @Override
    public void load() {

        List<CountryAndCompetitionResponse> countryAndCompetitionResponses = new ArrayList<>();

        Flux.fromStream(
                Arrays.asList(CountryCompetitions.values()).stream()
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
                s3Facade.put("", new ObjectMapper().writeValueAsString(countryAndCompetitionResponses));
                shutdownService.receive(CompetitionServiceImpl.class.getName());
            } catch (JsonProcessingException e) {
                log.error("json issue", e);
            }

        })
        .subscribe();

    }

}
