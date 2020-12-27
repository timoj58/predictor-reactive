package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.Competition;
import com.timmytime.predictorclientreactive.enumerator.CountryCompetitions;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.UpcomingCompetitionEventsResponse;
import com.timmytime.predictorclientreactive.model.UpcomingEventResponse;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

@Slf4j
@Service("fixtureService")
public class FixtureServiceImpl implements ILoadService {

    private final WebClientFacade webClientFacade;
    private final S3Facade s3Facade;
    private final ShutdownService shutdownService;
    private final TeamService teamService;

    private final String eventDataHost;

    @Autowired
    public FixtureServiceImpl(
            @Value("${clients.event-data}") String eventDataHost,
            S3Facade s3Facade,
            WebClientFacade webClientFacade,
            ShutdownService shutdownService,
            TeamService teamService
    ) {
        this.eventDataHost = eventDataHost;
        this.s3Facade = s3Facade;
        this.webClientFacade = webClientFacade;
        this.shutdownService = shutdownService;
        this.teamService = teamService;
    }


    @Override
    public void load() {
        Flux.fromStream(
                Stream.of(CountryCompetitions.values())
        ).doOnNext(country ->
                Flux.fromStream(country.getCompetitions().stream())
                        .subscribe(competition -> {
                            log.info("processing {}", competition);
                            UpcomingCompetitionEventsResponse upcomingCompetitionEventsResponse =
                                    new UpcomingCompetitionEventsResponse(
                                            Competition.valueOf(competition.toLowerCase()),
                                            new ArrayList<>());
                            webClientFacade.getUpcomingEvents(eventDataHost + "/events/" + competition)
                                    .doOnNext(event ->
                                            upcomingCompetitionEventsResponse.getUpcomingEventResponses().add(
                                                    UpcomingEventResponse.builder()
                                                            .away(teamService.getTeam(country.name().toLowerCase(), event.getAway()))
                                                            .home(teamService.getTeam(country.name().toLowerCase(), event.getHome()))
                                                            .country(country.name().toLowerCase())
                                                            .eventDate(event.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                                            .build()
                                            ))
                                    .doFinally(save -> {
                                        try {
                                            s3Facade.put("fixtures/" + competition, new ObjectMapper().writeValueAsString(upcomingCompetitionEventsResponse));
                                        } catch (JsonProcessingException e) {
                                            log.error("json", e);
                                        }
                                    })
                                    .subscribe();
                        })
        ).doFinally(end -> shutdownService.receive(FixtureServiceImpl.class.getName()))
                .subscribe();

    }
}
