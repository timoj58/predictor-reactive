package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.model.Team;
import com.timmytime.predictoreventdatareactive.service.EventOddsService;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import com.timmytime.predictoreventdatareactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.*;
import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service("betwayService")
public class BetwayService implements ProviderService {

    private final TeamService teamService;
    private final EventOddsService eventOddsService;

    @Override
    public void receive(JsonNode message) {

        JSONObject details = new JSONObject(message.toString());

        List<JSONObject> events = new ArrayList<>();

        JSONArray data = details.getJSONArray("data");

        IntStream.range(0, data.length()).forEach(i -> events.add(data.getJSONObject(i)));

        Flux.fromStream(
                events.stream()
        )
                .limitRate(1)
                .subscribe(event -> {

                    Optional<Team> homeTeam = teamService.find(event.getString("HomeTeamName"), details.getString("competition"));
                    Optional<Team> awayTeam = teamService.find(event.getString("AwayTeamName"), details.getString("competition"));

                    if (homeTeam.isPresent() && awayTeam.isPresent()) {

                        LocalDateTime eventDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                                event.getLong("Milliseconds")
                        ), ZoneId.systemDefault());
                        //dont have these details...TBC
                        if (eventDate.isBefore(LocalDateTime.now().plusDays(daysInAdvance()))) {

                            List<JSONObject> bets = new ArrayList<>();
                            IntStream.range(0, event.getJSONArray("bets").length()).forEach(
                                    bet -> bets.add(event.getJSONArray("bets").getJSONObject(bet)));

                            //reality.  filter out player / event odds.  we just want match type odds. i have data to do this.
                            //also saves hammering the DB.  best solution for now.

                            Flux.fromStream(
                                    bets.stream()
                            ).limitRate(1).subscribe(bet -> {
                                Double odds = bet.getDouble("OddsDecimal");

                                JSONObject eventName = new JSONObject();

                                eventName.put("title", bet.getString("title"));
                                eventName.put("CouponName", bet.getString("CouponName"));
                                eventName.put("BetName", bet.getString("BetName"));

                                eventOddsService.findEvent(
                                        Providers.BETWAY_ODDS.name(),
                                        eventName.toString(),
                                        eventDate,
                                        odds,
                                        Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId())
                                )
                                        .switchIfEmpty(Mono.just(new EventOdds()))
                                        .delayElement(Duration.ofMillis(10))
                                        .subscribe(eventOdds -> {
                                            if (eventOdds.getId() == null) {
                                                eventOdds.setId(UUID.randomUUID());
                                                eventOdds.setProvider(Providers.BETWAY_ODDS.name());
                                                eventOdds.setTeams(Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId()));
                                                eventOdds.setPrice(odds);
                                                eventOdds.setEventDate(eventDate);
                                                eventOdds.setEvent(eventName.toString());
                                                eventOdds.setCompetition(details.getString("competition"));
                                                //make a host stream.  simples.  then limit rate better.  or put tests in place.
                                                //then review this all TODO...

                                                eventOddsService.create(eventOdds).subscribe();
                                            }
                                        });

                            });

                        }

                    } else {
                        log.info("one or more {} teams nof : {}", details.getString("competition"), event.toString());
                    }
                });

    }

    private Integer daysInAdvance() {
        if (LocalDateTime.now().getDayOfWeek()
                .equals(DayOfWeek.TUESDAY)) {
            return 3;
        }
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
            return 5;
        }
        return 4;
    }

    @PostConstruct
    private void init() {
        eventOddsService.delete(Providers.BETWAY_ODDS).subscribe();
    }
}
