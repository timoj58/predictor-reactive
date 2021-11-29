package com.timmytime.predictorscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import com.timmytime.predictorscraperreactive.model.Lineup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.Optional;


@Slf4j
public class PlayerScraper {

    private final String matchUrl;

    public PlayerScraper(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public Triple<CompetitionFixtureCodes, ScraperType, String> createRequest(Pair<CompetitionFixtureCodes, Integer> matchId) {
        String url = matchUrl.replace("{game_id}", matchId.getRight().toString());
        return Triple.of(matchId.getLeft(), ScraperType.MATCH, url);
    }

    public Optional<Lineup> scrape(String url, String response) {
        var matchId = Integer.valueOf(url.substring(url.lastIndexOf("/") + 1));
        return process(Parser.htmlParser().parseInput(response, ""), matchId);
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

        try {
            lineups.get(0)
                    .select("tbody").get(0)
                    .select(".accordion-item")
                    .forEach(item -> addPlayerToLineup(data, "home", createPlayer(item)));

            lineups.get(1)
                    .select("tbody").get(0)
                    .select(".accordion-item")
                    .forEach(item -> addPlayerToLineup(data, "away", createPlayer(item)));

        } catch (Exception e) {
            log.error("lineup has failed for matchId {}", matchId);
            return setData(lineup, data);
        }

        return setData(lineup, data);

    }

    private void addPlayerToLineup(JSONObject data, String key, JSONObject player) {
        //dont add duplicates.  bug in source scrape
        boolean found = false;
        for (int i = 0; i < data.getJSONArray(key).length(); i++) {
            if (player.getString("espnId").equals(data.getJSONArray(key).getJSONObject(i).getString("espnId"))) {
                found = true;
            }
        }
        if (!found) {
            data.getJSONArray(key).put(player);
        }
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
