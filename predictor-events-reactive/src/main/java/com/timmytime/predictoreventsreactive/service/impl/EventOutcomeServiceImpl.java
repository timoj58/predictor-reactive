package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.repo.EventOutcomeRepo;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

@Service("eventOutcomeService")
public class EventOutcomeServiceImpl implements EventOutcomeService {

    private final EventOutcomeRepo eventOutcomeRepo;

    @Autowired
    public EventOutcomeServiceImpl(
            EventOutcomeRepo eventOutcomeRepo
    ){
        this.eventOutcomeRepo = eventOutcomeRepo;
    }

    @Override
    public Mono<EventOutcome> save(EventOutcome eventOutcome) {
        return eventOutcomeRepo.save(eventOutcome);
    }

    @Override
    public Mono<EventOutcome> find(UUID id) {
        return eventOutcomeRepo.findById(id);
    }

    @Override
    public Flux<EventOutcome> toValidate(String country) {
        return eventOutcomeRepo.findByCompetitionInAndSuccessNull(
                CountryCompetitions.valueOf(country.toUpperCase()).getCompetitions()
        );
    }

    @Override
    public Flux<EventOutcome> lastEvents(String country) {
        return eventOutcomeRepo.findByCompetitionInAndPreviousEventTrue(
                CountryCompetitions.valueOf(country.toUpperCase()).getCompetitions()
        );
    }

    @Override
    public Flux<EventOutcome> previousEvents(String competition) {
        return eventOutcomeRepo.findByCompetitionInAndPreviousEventTrue(
                Arrays.asList(competition)
        );
    }

    @Override
    public Flux<EventOutcome> currentEvents(String competition) {
        return eventOutcomeRepo.findByCompetitionInAndSuccessNull(
                Arrays.asList(competition)
        );
    }

    @Override
    public Flux<EventOutcome> previousEventsByTeam(UUID team) {
        return Flux.concat(
                eventOutcomeRepo.findByHomeOrderByDateDesc(team).take(6),
                eventOutcomeRepo.findByAwayOrderByDateDesc(team).take(6)
        );
    }

    @Override
    public Flux<EventOutcome> toFix() {
        return eventOutcomeRepo.findByPredictionNull();
    }
}
