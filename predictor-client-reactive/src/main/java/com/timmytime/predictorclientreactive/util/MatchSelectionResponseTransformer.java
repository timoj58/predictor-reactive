package com.timmytime.predictorclientreactive.util;

import com.timmytime.predictorclientreactive.enumerator.FantasyEventTypes;
import com.timmytime.predictorclientreactive.model.FantasyEvent;
import com.timmytime.predictorclientreactive.model.MatchSelectionResponse;
import com.timmytime.predictorclientreactive.model.PlayerResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class MatchSelectionResponseTransformer {

    private final Function<Map<Integer, Double>, Double> score = predictions -> {

        if (predictions.isEmpty()) {
            return 0.0;
        }

        return predictions.values().stream().mapToDouble(m -> m).sum();
    };

    private final BiFunction<List<PlayerEventScore>, FantasyEventTypes, MatchSelectionResponse> create = (playerEventScores, fantasyEventTypes) ->
            new MatchSelectionResponse(
                    fantasyEventTypes,
                    playerEventScores.stream()
                            .filter(f -> f.score > 0.0)
                            .sorted(Comparator.comparing(PlayerEventScore::getScore).reversed())
                            .map(m -> new PlayerResponse(
                                            m.getPlayerResponse(),
                                            new FantasyEvent(m.score, fantasyEventTypes.name().toLowerCase())
                                    )
                            )
                            .collect(Collectors.toList()));


    public List<MatchSelectionResponse> transform(List<PlayerResponse> combined) {

        List<MatchSelectionResponse> matchSelectionResponses = new ArrayList<>();


        List<PlayerEventScore> goals = new ArrayList<>();
        List<PlayerEventScore> assists = new ArrayList<>();
        List<PlayerEventScore> yellows = new ArrayList<>();

        //ok the hard part...so wait.  a bit.  need to filter the map..on combined totals pretty much.
        //or map the lot to a simple map of <PlayerId, Value> or each event. then select top 5 sorted....
        combined.forEach(player -> {

            goals.add(
                    new PlayerEventScore(player, player.getFantasyResponse()
                            .stream()
                            .mapToDouble(m -> score.apply(m.getGoals())).findFirst().orElse(0.0))
            );

            assists.add(
                    new PlayerEventScore(player, player.getFantasyResponse()
                            .stream()
                            .mapToDouble(m -> score.apply(m.getAssists())).findFirst().orElse(0.0))
            );

            yellows.add(
                    new PlayerEventScore(player, player.getFantasyResponse()
                            .stream()
                            .mapToDouble(m -> score.apply(m.getYellowCards())).findFirst().orElse(0.0))
            );

        });


        matchSelectionResponses.add(create.apply(goals, FantasyEventTypes.GOALS));
        matchSelectionResponses.add(create.apply(assists, FantasyEventTypes.ASSISTS));
        matchSelectionResponses.add(create.apply(yellows, FantasyEventTypes.YELLOW_CARD));

        return matchSelectionResponses.stream().sorted(Comparator.comparing(MatchSelectionResponse::getOrder)).collect(Collectors.toList());
    }

    @Getter
    @Setter
    private static class PlayerEventScore {

        private PlayerResponse playerResponse;
        private Double score;

        public PlayerEventScore(
                PlayerResponse playerResponse,
                Double score
        ) {
            this.playerResponse = playerResponse;
            this.score = score;
        }

    }
}
