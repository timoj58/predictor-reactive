package com.timmytime.predictorscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.model.Lineup;
import com.timmytime.predictorscraperreactive.service.ScraperTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Slf4j
public class PlayerScraper {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String matchUrl;
    private final ScraperTrackerService scraperTrackerService;

    public PlayerScraper(String matchUrl, ScraperTrackerService scraperTrackerService) {
        this.matchUrl = matchUrl;
        this.scraperTrackerService = scraperTrackerService;
    }

    public Optional<Lineup> scrape(Pair<CompetitionFixtureCodes, Integer> matchId) {

        String url = matchUrl.replace("{game_id}", matchId.getRight().toString());
        String response = "";
        try {
            response = restTemplate.exchange(url,
                    HttpMethod.GET, null, String.class).getBody();
        } catch (RestClientException restClientException) {
            scraperTrackerService.addFailedPlayersRequest(matchId);
            return Optional.empty();
        }

        return process(Parser.htmlParser().parseInput(response, ""), matchId.getRight());
    }

    private Optional<Lineup> process(Document document, Integer matchId) {
        Lineup lineup = new Lineup();
        lineup.setMatchId(matchId);
        lineup.setType("lineup");

        JSONObject data = new JSONObject();
        data.put("home", new JSONArray());
        data.put("away", new JSONArray());

        Elements lineups = document.select(".column-one");

        if (lineups.size() < 2) {
            return setData(lineup, data);
        }

        lineups.get(0)
                .select("tbody").get(0)
                .select(".accordion-item")
                .forEach(item -> data.getJSONArray("home").put(createPlayer(item)));

        lineups.get(1)
                .select("tbody").get(0)
                .select(".accordion-item")
                .forEach(item -> data.getJSONArray("away").put(createPlayer(item)));

        return setData(lineup, data);

    }

    private JSONObject createPlayer(Element element) {
        var name = element.select(".lineup-player a").text();
        var espnId = element.select(".lineup-player a").attr("data-player-uid");
        var goals = element.select("[data-stat='totalGoals']").text();
        var assists = element.select(".stats .stat .value[data-stat='goalAssists']").text();
        var yellows = element.select(".stats .stat .value[data-stat='yellowCards']").text();

        return new JSONObject()
                .put("name", name)
                .put("espnId", espnId)
                .put("goals", goals.trim().isEmpty() ? "0" : goals)
                .put("assists", assists.trim().isEmpty() ? "0" : assists)
                .put("yellows", yellows.trim().isEmpty() ? "0" : yellows);
    }

    private Optional<Lineup> setData(Lineup lineup, JSONObject data) {
        try {
            lineup.setData(new ObjectMapper().readTree(data.toString()));
        } catch (JsonProcessingException e) {
            log.error("json issue", e);
        }

        return Optional.of(lineup);
    }
}
