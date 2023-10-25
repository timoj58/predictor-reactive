package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.model.Match;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Slf4j
@Service("validationService")
public class ValidationServiceImpl implements ValidationService {

    private final EventOutcomeService eventOutcomeService;
    private final WebClientFacade webClientFacade;

    private final String dataHost;

    @Autowired
    public ValidationServiceImpl(
            @Value("${clients.data}") String dataHost,
            EventOutcomeService eventOutcomeService,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.eventOutcomeService = eventOutcomeService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void validate(String country) {

        log.info("validating {}", country);

        eventOutcomeService.toValidate(country)
                .subscribe(eventOutcome ->
                        webClientFacade.getMatch(dataHost
                                        + "/match?home=" + eventOutcome.getHome()
                                        + "&away=" + eventOutcome.getAway()
                                        + "&date=" + eventOutcome.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                .subscribe(match -> {

                                    String prediction = predictionResult(eventOutcome);

                                    switch (Predictions.valueOf(eventOutcome.getEventType())) {
                                        case PREDICT_RESULTS -> eventOutcome.setSuccess(validateResult(prediction, match));
                                        case PREDICT_GOALS -> eventOutcome.setSuccess(validateGoals(prediction, match));
                                    }

                                    eventOutcome.setPreviousEvent(Boolean.TRUE);
                                    eventOutcomeService.save(eventOutcome).subscribe();

                                })
                );
    }

    @Override
    public void resetLast(String country, Consumer<String> doFinally) {

        log.info("resetting last outcome for {}", country);

        eventOutcomeService.lastEvents(country)
                .doOnNext(previousEvent -> {
                    previousEvent.setPreviousEvent(Boolean.FALSE);
                    eventOutcomeService.save(previousEvent).subscribe();
                })
                .doFinally(then -> doFinally.accept(country))
                .subscribe();
    }


    private String predictionResult(EventOutcome eventOutcome) {

        JSONArray results = legacyShit(eventOutcome.getPrediction());

        if (Predictions.valueOf(eventOutcome.getEventType()).equals(Predictions.PREDICT_GOALS)) {
            Double weightedGoals = 0.0;
            for (int i = 0; i < results.length(); i++) {
                if (results.getJSONObject(i).getDouble("score") > 0.0) {
                    weightedGoals += (results.getJSONObject(i).getDouble("key") * (results.getJSONObject(i).getDouble("score") / 100));
                }
            }

            return String.valueOf(weightedGoals);

        } else {
            return results.getJSONObject(0).getString("key");
        }

    }

    private Boolean validateGoals(String prediction, Match match) {

        boolean over = Double.valueOf(prediction) >= 2.5;

        if (over) {
            return match.getHomeScore() + match.getAwayScore() >= 2.5;
        } else {
            return match.getHomeScore() + match.getAwayScore() < 2.5;
        }

    }

    private Boolean validateResult(String prediction, Match match) {

        switch (prediction) {
            case "homeWin":
                return match.getHomeScore() > match.getAwayScore();
            case "awayWin":
                return match.getAwayScore() > match.getHomeScore();
            case "draw":
                return match.getAwayScore().intValue() == match.getHomeScore().intValue();
            default:
                return Boolean.FALSE;
        }
    }

    //TODO remove.  lost all this data now.
    private JSONArray legacyShit(String prediction) {
        try {
            return new JSONObject(prediction).getJSONArray("result");

        } catch (Exception e) {
            return new JSONArray(prediction);
        }
    }
}
