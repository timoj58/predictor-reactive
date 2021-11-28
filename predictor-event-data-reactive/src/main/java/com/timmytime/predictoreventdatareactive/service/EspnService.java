package com.timmytime.predictoreventdatareactive.service;

import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.facade.WebClientFacade;
import com.timmytime.predictoreventdatareactive.model.EventOdds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class EspnService {

    private final WebClientFacade webClientFacade;
    private final EventOddsService eventOddsService;
    private final String dataHost;

    @Autowired
    public EspnService(
            @Value("${clients.data}") String dataHost,
            WebClientFacade webClientFacade,
            EventOddsService eventOddsService
    ) {
        this.dataHost = dataHost;
        this.webClientFacade = webClientFacade;
        this.eventOddsService = eventOddsService;
    }

    public Pair<List<JSONObject>, Consumer<JSONObject>> prepareWrapper(JSONObject event) {
        log.info("processing {}", event.toString());
        try {
            return prepare(event);
        } catch (Exception ex) {
            log.error("failed to prepare", ex);
            return Pair.of(Arrays.asList(), e -> {
            });
        }
    }

    private Pair<List<JSONObject>, Consumer<JSONObject>> prepare(JSONObject event) {
        String competition = event.getString("competition");

        var home = event.getJSONObject("data").getString("home");
        var away = event.getJSONObject("data").getString("away");

        var matchTeams = webClientFacade.getMatchTeams(dataHost + "/match/teams/" + competition + "?home=" + home + "&away=" + away);

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
                                .matchTeams(matchTeams)
                                .eventDate(eventDate)
                                .event(eventName.toString())
                                .competition(competition)
                                .build()
                );
            });

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

    public void init() {
        eventOddsService.delete(Providers.ESPN_ODDS).subscribe();
    }
}
