package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class ResultScraper {


    public Triple<CompetitionFixtureCodes, ScraperType, String> createRequest(Pair<CompetitionFixtureCodes, String> competition, LocalDate date) {
        String url = competition.getRight().replace("{date}", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).replace("-", ""));
        return Triple.of(competition.getLeft(), ScraperType.RESULTS, url);
    }

    public List<Result> scrape(CompetitionFixtureCodes competition, String response) {
        return process(Parser.htmlParser().parseInput(response, ""), competition);
    }

    private List<Result> process(Document document, CompetitionFixtureCodes competition) {
        List<Result> results = new ArrayList<>();

        document.head().getAllElements().stream().filter(f -> f.tag().toString().equals("script"))
                .filter(f -> f.data().contains("window.espn.scoreboardData")).findFirst().ifPresent(
                        then -> {

                            JSONArray events = new JSONObject(then.data().substring(then.data().indexOf("{"))
                                    .replace("<script>", "")
                                    .replace("</script>", "")).getJSONArray("events");

                            IntStream.range(0, events.length()).forEach(
                                    index -> {
                                        var event = events.getJSONObject(index);
                                        var status = event.getJSONObject("status").getJSONObject("type").getString("name");
                                        if (status.equals("STATUS_FULL_TIME")) {
                                            results.add(create(event, competition));
                                        }
                                    }
                            );
                        });

        return results;
    }

    private Result create(JSONObject event, CompetitionFixtureCodes competition) {
        Result result = new Result();

        JSONArray teams = event.getJSONArray("competitions").getJSONObject(0).getJSONArray("competitors");
        JSONObject homeTeam = teams.getJSONObject(0);
        JSONObject awayTeam = teams.getJSONObject(1);


        result.setType("result");
        result.setMatchId(event.getInt("id"));
        result.setDate(event.getString("date"));
        result.setCompetition(competition.name().toLowerCase());
        result.setCountry(competition.name().toLowerCase().split("_")[0]);
        result.setHomeTeam(homeTeam.getJSONObject("team").getString("displayName"));
        result.setAwayTeam(awayTeam.getJSONObject("team").getString("displayName"));
        result.setHomeTeamEspnId(homeTeam.getString("id"));
        result.setAwayTeamEspnId(awayTeam.getString("id"));
        result.setHomeScore(homeTeam.getInt("score"));
        result.setAwayScore(awayTeam.getInt("score"));

        return result;
    }

}
