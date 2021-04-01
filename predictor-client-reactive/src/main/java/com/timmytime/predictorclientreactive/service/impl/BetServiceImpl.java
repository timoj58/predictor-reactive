package com.timmytime.predictorclientreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorclientreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorclientreactive.facade.IS3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.model.EventOutcome;
import com.timmytime.predictorclientreactive.model.FantasyOutcome;
import com.timmytime.predictorclientreactive.model.TopSelection;
import com.timmytime.predictorclientreactive.service.ILoadService;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import com.timmytime.predictorclientreactive.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service("betService")
public class BetServiceImpl implements ILoadService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final IS3Facade s3Facade;
    private final WebClientFacade webClientFacade;
    private final ShutdownService shutdownService;
    private final TeamService teamService;
    @Value("${betting.home-win}")
    private Integer homeWin;
    @Value("${betting.away-win}")
    private Integer awayWin;
    @Value("${betting.draw}")
    private Integer draw;
    @Value("${betting.goal}")
    private Integer goal;
    @Value("${betting.assist}")
    private Integer assist;
    @Value("${betting.yellow-card}")
    private Integer yellowCard;
    @Value("${clients.event}")
    private String eventHost;
    @Value("${clients.players}")
    private String playersHost;

    @Override
    public void load() {

        log.info("loading bets...");
        Flux.fromStream(
                Stream.of(Pair.of(FantasyEventTypes.GOALS, goal), Pair.of(FantasyEventTypes.ASSISTS, assist), Pair.of(FantasyEventTypes.YELLOW_CARD, yellowCard))
        ).doOnNext(market -> {
                    List<TopSelection> topSelections = new ArrayList<>();
                    log.info("processing {}", market.getLeft().name());
                    webClientFacade.topPlayerSelections(playersHost + "/top-selections/" + market.getLeft().name() + "?threshold=" + market.getRight())
                            .doOnNext(event -> topSelections.add(processPlayerEvent(event)))
                            .doFinally(save -> Mono.just(topSelections).delayElement(Duration.ofSeconds(10)).subscribe(
                                    finish -> finish(finish, market.getLeft().name())
                            ))
                            .subscribe();
                }
        )
                .doFinally(results ->
                        Flux.fromStream(
                                Stream.of(Pair.of("homeWin", homeWin), Pair.of("awayWin", awayWin), Pair.of("draw", draw))
                        ).doOnNext(outcome -> {
                                    List<TopSelection> topSelections = new ArrayList<>();
                                    log.info("processing {}", outcome.getLeft());
                                    webClientFacade.topMatchSelections(eventHost + "/top-selections/" + outcome.getLeft() + "?threshold=" + outcome.getRight())
                                            .doOnNext(selection -> topSelections.add(processMatchEvent(selection, outcome.getLeft())))
                                            .doFinally(save -> Mono.just(topSelections).delayElement(Duration.ofSeconds(10)).subscribe(
                                                    finish -> finish(finish, outcome.getLeft())
                                            ))
                                            .subscribe();
                                }
                        )
                                .doFinally(finish -> Mono.just(BetServiceImpl.class.getName())
                                        .delayElement(Duration.ofMinutes(1))
                                        .subscribe(shutdownService::receive)
                                ).subscribe()
                ).subscribe();

    }

    private void finish(List<TopSelection> topSelections, String market) {
        log.info("saving bets");
        try {
            s3Facade.put("selected-bets/" + market.toLowerCase(), new ObjectMapper().writeValueAsString(topSelections));
        } catch (JsonProcessingException e) {
            log.error("json processing error");
        }
    }


    private TopSelection processPlayerEvent(FantasyOutcome fantasyOutcome) {
        var prediction = convert(fantasyOutcome.getPrediction());
        return TopSelection.builder()
                .label(fantasyOutcome.getLabel())
                .subtitle("vs " + teamService.getTeam(fantasyOutcome.getOpponent()).getLabel())
                .rating(getPlayerScore(prediction))
                .market(fantasyOutcome.getFantasyEventType().name())
                .date(fantasyOutcome.getEventDate().format(dateTimeFormatter))
                .build();
    }

    private TopSelection processMatchEvent(EventOutcome eventOutcome, String outcome) {
        var prediction = convert(eventOutcome.getPrediction());
        var title = Arrays.asList("homeWin", "draw").contains(outcome) ? eventOutcome.getHome() : eventOutcome.getAway();
        var subtitle = Arrays.asList("awayWin").contains(outcome) ? eventOutcome.getHome() : eventOutcome.getAway();
        return TopSelection.builder()
                .label(teamService.getTeam(eventOutcome.getCountry(), title).getLabel())
                .subtitle("vs " + teamService.getTeam(eventOutcome.getCountry(), subtitle).getLabel())
                .rating(prediction.getJSONObject(0).getDouble("score"))
                .market(prediction.getJSONObject(0).getString("key"))
                .date(eventOutcome.getDate().format(dateTimeFormatter))
                .build();
    }

    private JSONArray convert(String prediction) {
        try {
            return new JSONObject(prediction).getJSONArray("result");
        } catch (Exception e) {
            return new JSONArray(prediction);
        }
    }

    private Double getPlayerScore(JSONArray prediction) {
        double score = 0.0;

        for (int i = 0; i < prediction.length(); i++) {
            if (!prediction.getJSONObject(i).getString("key").equals("0")) {
                score += prediction.getJSONObject(i).getDouble("score");
            }
        }

        return score;
    }


}
