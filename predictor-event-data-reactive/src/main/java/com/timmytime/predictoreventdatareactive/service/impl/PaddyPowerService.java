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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
@Service("paddyPowerService")
public class PaddyPowerService implements ProviderService {

    private final TeamService teamService;
    private final EventOddsService eventOddsService;

    private Consumer<JsonNode> receive;
    private final Flux<JsonNode> events;

    @Autowired
    public PaddyPowerService(
            TeamService teamService,
            EventOddsService eventOddsService
    )
    {
        this.teamService = teamService;
        this.eventOddsService = eventOddsService;

        this.events = Flux.push(sink ->
                PaddyPowerService.this.receive = (t) -> sink.next(t), FluxSink.OverflowStrategy.BUFFER);

        this.events.limitRate(1).subscribe(this::process);

    }

    @Override
    public void receive(JsonNode message) {
        receive.accept(message);
    }

    private void process(JsonNode message) {
        log.info("processing {}", message.toString());

        JSONObject details = new JSONObject(message.toString());

        List<JSONObject> events = new ArrayList<>();

        JSONArray data = details.getJSONArray("data");

        IntStream.range(0, data.length()).forEach(i -> events.add(data.getJSONObject(i)));

        Flux.fromStream(
                events.stream()
        )
                .limitRate(5)
                .subscribe(event -> {
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


                            Optional<Team> homeTeam = teamService.find(homeEvent.getString("runnerName"), details.getString("competition"));
                            Optional<Team> awayTeam = teamService.find(awayEvent.getString("runnerName"), details.getString("competition"));

                            if (homeTeam.isPresent() && awayTeam.isPresent()) {
                                Flux.fromStream(
                                        Arrays.asList(homeEvent, awayEvent, drawEvent).stream()
                                )
                                        .subscribe(bet -> {
                                                    //reality.  filter out player / event odds.  we just want match type odds. i have data to do this.
                                                    //also saves hammering the DB.  best solution for now.

                                                    JSONObject eventDetails = new JSONObject().put("type", bet.get("type"));
                                                    eventOddsService.findEvent(
                                                            Providers.PADDYPOWER_ODDS.name(),
                                                            eventDetails.toString(),
                                                            eventDate,
                                                            bet.getDouble("decimalOdds"),
                                                            Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId()))
                                                            .switchIfEmpty(Mono.just(new EventOdds()))
                                                            .delayElement(Duration.ofMillis(10))
                                                            .subscribe(newBet -> {
                                                                if (newBet.getId() == null) {
                                                                    newBet.setId(UUID.randomUUID());
                                                                    newBet.setProvider(Providers.PADDYPOWER_ODDS.name());
                                                                    newBet.setPrice(bet.getDouble("decimalOdds"));
                                                                    newBet.setEventDate(eventDate);
                                                                    newBet.setCompetition(details.getString("competition"));
                                                                    newBet.setTeams(Arrays.asList(homeTeam.get().getId(), awayTeam.get().getId()));
                                                                    newBet.setEvent(eventDetails.toString());

                                                                    eventOddsService.create(newBet).subscribe();
                                                                }
                                                            });
                                                }
                                        );
                            } else {
                                log.info("one or more {} teams is nof : {}", details.getString("competition"), event.toString());
                            }
                        }
                    }
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
