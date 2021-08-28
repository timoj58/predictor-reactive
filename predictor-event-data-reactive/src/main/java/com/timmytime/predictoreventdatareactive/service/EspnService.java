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

@Service
@Slf4j
@RequiredArgsConstructor
public class EspnService {

    private final TeamService teamService;
    private final EventOddsService eventOddsService;

    public Pair<List<JSONObject>, Consumer<JSONObject>> prepareWrapper(JSONObject event) {
        log.info("processing {}", event.toString());
        try{
            return prepare(event);
        }catch (Exception ex){
            log.error("failed to prepare", ex);
            return Pair.of(Arrays.asList(), e -> { });
        }
    }

    private Pair<List<JSONObject>, Consumer<JSONObject>> prepare(JSONObject event)  {
        String competition = event.getString("competition");

        Optional<Team> homeTeam = teamService.find(event.getJSONObject("data").getString("home"), competition);
        Optional<Team> awayTeam = teamService.find(event.getJSONObject("data").getString("away"), competition);

        if (homeTeam.isPresent() && awayTeam.isPresent()) {

            LocalDateTime eventDate = LocalDateTime.ofEpochSecond(
                    event.getJSONObject("data").getLong("milliseconds"), 0,
                    OffsetDateTime.now().getOffset());

            if (eventDate.isBefore(LocalDateTime.now().plusDays(daysInAdvance()))) {

                List<JSONObject> bets = new ArrayList<>();
                bets.add(new JSONObject());

                return Pair.of(bets, bet -> {

                    JSONObject eventName = new JSONObject();

                    eventName.put("title", "RESULT");

                    eventOddsService.addToQueue(
                            EventOdds.builder()
                                    .id(UUID.randomUUID())
                                    .provider(Providers.ESPN_ODDS.name())
                                    .teams(Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId()))
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

        return Pair.of(Arrays.asList(), e -> { });
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
        eventOddsService.delete(Providers.ESPN_ODDS).subscribe();
    }
}
