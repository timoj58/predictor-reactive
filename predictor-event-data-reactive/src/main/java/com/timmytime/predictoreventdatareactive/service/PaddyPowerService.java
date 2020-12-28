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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service("paddyPowerService")
public class PaddyPowerService {

    private final TeamService teamService;
    private final EventOddsService eventOddsService;

    public Pair<List<JSONObject>, Consumer<JSONObject>> prepare(JSONObject event) {
        log.info("processing {}", event.toString());
        String competition = event.getString("competition");

        List<JSONObject> eventOutcomes = new ArrayList<>();

        IntStream.range(0, event.getJSONObject("results").getJSONArray("outcomes").length()).forEach(
                i -> eventOutcomes.add(event.getJSONObject("results").getJSONArray("outcomes").getJSONObject(i))
        );


        //some empty responses can skip as no data to process..
        if (!eventOutcomes.isEmpty()) {

            String date = event.getJSONObject("results").getString("marketTime");

            LocalDateTime eventDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            if (eventDate.isBefore(LocalDateTime.now().plusDays(daysInAdvance()))) {

                JSONObject homeEvent = eventOutcomes.stream().filter(f -> f.getString("type").equals("HOME")).findFirst().get();
                JSONObject awayEvent = eventOutcomes.stream().filter(f -> f.getString("type").equals("AWAY")).findFirst().get();
                JSONObject drawEvent = eventOutcomes.stream().filter(f -> f.getString("type").equals("DRAW")).findFirst().get();


                Optional<Team> homeTeam = teamService.find(homeEvent.getString("runnerName"), competition);
                Optional<Team> awayTeam = teamService.find(awayEvent.getString("runnerName"), competition);

                if (homeTeam.isPresent() && awayTeam.isPresent()) {

                    return Pair.of(Arrays.asList(homeEvent, awayEvent, drawEvent), bet ->
                    {

                        JSONObject eventDetails = new JSONObject().put("type", bet.get("type"));

                        eventOddsService.addToQueue(
                                EventOdds.builder()
                                        .id(UUID.randomUUID())
                                        .provider(Providers.PADDYPOWER_ODDS.name())
                                        .price(bet.getDouble("decimalOdds"))
                                        .eventDate(eventDate)
                                        .competition(competition)
                                        .teams(Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId()))
                                        .event(eventDetails.toString())
                                        .build()
                        );
                    });


                } else {
                    log.info("one or more {} teams is nof : {}", competition, event.toString());
                }
            }
        }
        return Pair.of(Arrays.asList(), e -> {
        });
    }

    private Integer daysInAdvance() {
        if (LocalDateTime.now().getDayOfWeek()
                .equals(DayOfWeek.TUESDAY)) {
            return 3;
        }
        return 4;
    }

    @PostConstruct
    private void init() {
        eventOddsService.delete(Providers.PADDYPOWER_ODDS).subscribe();
    }

}
