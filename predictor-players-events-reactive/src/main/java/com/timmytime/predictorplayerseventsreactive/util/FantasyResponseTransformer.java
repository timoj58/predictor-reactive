package com.timmytime.predictorplayerseventsreactive.util;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorplayerseventsreactive.model.FantasyOutcome;
import com.timmytime.predictorplayerseventsreactive.model.FantasyResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class FantasyResponseTransformer {


    private final BiFunction<List<FantasyOutcome>, Predicate<FantasyOutcome>, String> filter = (outcomes, predicate) ->
            outcomes.stream()
                    .filter(predicate)
                    .findFirst()
                    .orElseThrow()
                    .getPrediction();

    public BiFunction<List<FantasyOutcome>, FantasyEventTypes, Double> total = (outcomes, event) -> {
        JSONObject result = new JSONObject().put("accumulator", 0.0);
        switch (event) {
            case SAVES:
            case MINUTES:
                outcomes.forEach(f -> result.put("accumulator", result.getDouble("accumulator") + getAverage(f.getPrediction())));
                break;
            default:
                outcomes.forEach(f -> result.put("accumulator", result.getDouble("accumulator") + getScores(f.getPrediction()).values().stream().mapToDouble(m -> m).sum()));
        }

        return result.getDouble("accumulator");
    };

    public Function<List<FantasyOutcome>, FantasyResponse> transform = outcomes -> {
        FantasyResponse fantasyResponse = new FantasyResponse();


        try {
            fantasyResponse.setMinutes(getAverage(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.MINUTES))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }


        try {
            fantasyResponse.setAssists(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.ASSISTS))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }

        try {
            fantasyResponse.setGoals(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }

        try {
            fantasyResponse.setSaves(getAverage(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.SAVES))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }

        try {
            fantasyResponse.setConceded(getAverage(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS_CONCEDED))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }

        try {
            fantasyResponse.setRedCards(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.RED_CARD))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }

        try {
            fantasyResponse.setYellowCards(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.YELLOW_CARD))
            ));
        } catch (Exception e) {
            fantasyResponse.setSaves(null);
        }


        return fantasyResponse;
    };

    private Double getAverage(String prediction) {
        JSONArray results = legacyIssue(prediction);

        //weight the total and dont ceil it....for fuck sake,  no wonder! its 50/50 now. well it was...now its really accurate.
        Double weightedGoals = 0.0;
        for (int i = 0; i < results.length(); i++) {
            if (results.getJSONObject(i).getDouble("score") > 0.0) {
                weightedGoals += (results.getJSONObject(i).getDouble("key") * (results.getJSONObject(i).getDouble("score") / 100));
            }
        }

        return weightedGoals;

    }

    private Map<Integer, Double> getScores(String prediction) {


        Map<Integer, Double> result = new HashMap<>();

        JSONArray results = legacyIssue(prediction);

        for (int i = 0; i < results.length(); i++) {

            JSONObject r = results.getJSONObject(i);

            if (r.getInt("key") != 0 && r.getDouble("score") >= 1.0) {
                result.put(r.getInt("key"), r.getDouble("score"));
            }
        }

        return result;
    }

    private JSONArray legacyIssue(String prediction) {
        try {
            return new JSONObject(prediction).getJSONArray("result");
        } catch (Exception e) {
            return new JSONArray(prediction);
        }
    }

}
