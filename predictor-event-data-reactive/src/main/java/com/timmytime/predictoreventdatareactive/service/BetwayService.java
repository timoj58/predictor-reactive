package com.timmytime.predictoreventdatareactive.service;

import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import com.timmytime.predictoreventdatareactive.model.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service("betwayService")
public class BetwayService {

    private final TeamService teamService;
    private final EventOddsService eventOddsService;

    public Pair<List<JSONObject>, Consumer<JSONObject>> prepare(JSONObject event) {
        log.info("processing {}", event.toString());
        String competition = event.getString("competition");


        Optional<Team> homeTeam = teamService.find(event.getString("HomeTeamName"), competition);
        Optional<Team> awayTeam = teamService.find(event.getString("AwayTeamName"), competition);

        if (homeTeam.isPresent() && awayTeam.isPresent()) {

            LocalDateTime eventDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                    event.getLong("Milliseconds")
            ), ZoneId.systemDefault());
            //dont have these details...TBC
            if (eventDate.isBefore(LocalDateTime.now().plusDays(daysInAdvance()))) {

                List<JSONObject> bets = new ArrayList<>();
                IntStream.range(0, event.getJSONArray("bets").length()).forEach(
                        bet -> bets.add(event.getJSONArray("bets").getJSONObject(bet)));


                return Pair.of(bets, bet -> {
                    Double odds = bet.getDouble("OddsDecimal");

                    JSONObject eventName = new JSONObject();

                    eventName.put("title", bet.getString("title"));
                    eventName.put("CouponName", bet.getString("CouponName"));
                    eventName.put("BetName", bet.getString("BetName"));


                    eventOddsService.addToQueue(
                            EventOdds.builder()
                                    .id(UUID.randomUUID())
                                    .provider(Providers.BETWAY_ODDS.name())
                                    .teams(Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId()))
                                    .price(odds)
                                    .eventDate(eventDate)
                                    .event(eventName.toString())
                                    .competition(competition)
                                    .build()
                    );
                });

            }

        } else {
            log.info("one or more {} teams nof : {}", competition, event.toString());
        }

        return Pair.of(Arrays.asList(), e -> {
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
