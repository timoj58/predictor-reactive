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
        outcomes.forEach(f -> result.put("accumulator", result.getDouble("accumulator") + getScores(f.getPrediction()).values().stream().mapToDouble(m -> m).sum()));

        return result.getDouble("accumulator");
    };

    public Function<List<FantasyOutcome>, FantasyResponse> transform = outcomes -> {
        FantasyResponse fantasyResponse = new FantasyResponse();


        try {
            fantasyResponse.setAssists(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.ASSISTS))
            ));
        } catch (Exception e) {
            fantasyResponse.setAssists(null);
        }

        try {
            fantasyResponse.setGoals(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.GOALS))
            ));
        } catch (Exception e) {
            fantasyResponse.setGoals(null);
        }

        try {
            fantasyResponse.setYellowCards(getScores(
                    filter.apply(outcomes, (f) -> f.getFantasyEventType().equals(FantasyEventTypes.YELLOW_CARD))
            ));
        } catch (Exception e) {
            fantasyResponse.setYellowCards(null);
        }


        return fantasyResponse;
    };


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
