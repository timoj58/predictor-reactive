package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.model.Result;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class ResultScraper {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ScraperTrackerService scraperTrackerService;

    public ResultScraper(ScraperTrackerService scraperTrackerService) {
        this.scraperTrackerService = scraperTrackerService;
    }

    public List<Result> scrape(Pair<CompetitionFixtureCodes, String> competition, LocalDate date) {

        String url = competition.getRight().replace("{date}", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).replace("-", ""));
        String response = "";
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, null, String.class).getBody();
        } catch (RestClientException restClientException) {
            scraperTrackerService.addFailedResultsRequest(competition, date);
            return Collections.emptyList();
        }

        return process(Parser.htmlParser().parseInput(response, ""), competition.getLeft());
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
                            index -> results.add(create(events.getJSONObject(index), competition))
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
