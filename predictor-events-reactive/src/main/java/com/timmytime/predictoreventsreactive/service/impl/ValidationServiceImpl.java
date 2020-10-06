package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.model.Event;
import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.model.Match;
import com.timmytime.predictoreventsreactive.service.EventOutcomeService;
import com.timmytime.predictoreventsreactive.service.ValidationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service("validationService")
public class ValidationServiceImpl implements ValidationService {

    private final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);
    private final EventOutcomeService eventOutcomeService;
    private final WebClientFacade webClientFacade;

    private final String dataHost;

    @Autowired
    public ValidationServiceImpl(
            @Value("${data.host}") String dataHost,
            EventOutcomeService eventOutcomeService,
            WebClientFacade webClientFacade
    ){
        this.dataHost = dataHost;
        this.eventOutcomeService = eventOutcomeService;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void validate(String country) {

        log.info("validating {}", country);

        eventOutcomeService.toValidate(country)
                .subscribe(eventOutcome ->
                        webClientFacade.getMatch(dataHost+"/match?home="
                                +eventOutcome.getHome()+"&away="
                                +eventOutcome.getAway()
                                +"&date="+ eventOutcome.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .subscribe(match -> {

                            String prediction = predictionResult(eventOutcome);

                            switch (Predictions.valueOf(eventOutcome.getEventType())){
                                case PREDICT_RESULTS:
                                    eventOutcome.setSuccess(validateResult(prediction, match));
                                    break;
                                case PREDICT_GOALS:
                                    eventOutcome.setSuccess(validateGoals(prediction, match));
                                    break;
                            }

                            eventOutcome.setPreviousEvent(Boolean.TRUE);
                            eventOutcomeService.save(eventOutcome).subscribe();

                        })
                );
    }

    @Override
    public Flux<EventOutcome> resetLast(String country) {

        return eventOutcomeService.lastEvents(country)
                .doOnNext(previousEvent -> {
                    previousEvent.setPreviousEvent(Boolean.FALSE);
                    eventOutcomeService.save(previousEvent).subscribe();
                });
    }


    private String predictionResult(EventOutcome eventOutcome){

        JSONArray results = legacyShit(eventOutcome.getPrediction());

        if (Predictions.valueOf(eventOutcome.getEventType()).equals(Predictions.PREDICT_GOALS)) {
            Double weightedGoals = 0.0;
            for(int i=0;i<results.length();i++){
                if (results.getJSONObject(i).getDouble("score") > 0.0) {
                    weightedGoals += (results.getJSONObject(i).getDouble("key") * (results.getJSONObject(i).getDouble("score") / 100));
                }
            }

            return String.valueOf(weightedGoals);

        } else {
            return results.getJSONObject(0).getString("key");
        }

    }

    private Boolean validateGoals(String prediction, Match match){

        boolean over = Double.valueOf(prediction) >= 2.5;

        if(over){
            return match.getHomeScore()+match.getAwayScore() >= 2.5;
        }else{
            return match.getHomeScore()+match.getAwayScore() < 2.5;
        }

    };

    private Boolean validateResult(String prediction, Match match){

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

    private JSONArray legacyShit(String prediction){
        try{
            return new JSONObject(prediction).getJSONArray("result");

        }catch (Exception e){
            return new JSONArray(prediction);
        }
    }
}
