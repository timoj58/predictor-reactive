package com.timmytime.predictoreventdatareactive.service.impl;

import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.repo.EventOddsRepo;
import com.timmytime.predictoreventdatareactive.response.Event;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("eventOddsService")
public class EventOddsServiceImpl implements EventOddsService {

    private final EventOddsRepo eventOddsRepo;

    @Autowired
    public EventOddsServiceImpl(
            EventOddsRepo eventOddsRepo
    ) {
        this.eventOddsRepo = eventOddsRepo;
    }

    @Override
    public Mono<EventOdds> create(EventOdds eventOdds) {
        return eventOddsRepo.save(eventOdds);
    }

    @Override
    public Mono<EventOdds> findEvent(String provider, String event, LocalDateTime eventDate, Double price, List<UUID> teams) {
        return eventOddsRepo.findByProviderAndEventAndEventDateAndPriceAndTeamsContains(provider, event, eventDate, price, teams);
    }

    @Override
    public Mono<Void> delete(Providers providers) {
        return eventOddsRepo.deleteByProviderAndEventDateBefore(providers.name(), LocalDate.now().atStartOfDay());
    }

    @Override
    public Flux<Event> getEvents(String competition) {
        return eventOddsRepo.findByCompetition(competition)
                .distinct(EventOdds::getTeams)
                .map(Event::new);
    }
}
