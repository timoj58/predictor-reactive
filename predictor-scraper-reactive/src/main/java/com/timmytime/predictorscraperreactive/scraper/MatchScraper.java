package com.timmytime.predictorscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.Match;
import com.timmytime.predictorscraperreactive.util.ScraperUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
public class MatchScraper implements IScraper<Match> {
    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory;
    private final RestTemplate restTemplate = new RestTemplate();
    Function<String, JsonNode> parse = data -> {
        JSONObject parse = new JSONObject();

        Document document = Parser.htmlParser().parseInput(data, "");

        //the actual result is easy.
        parse.put("value", document.text());

        String classes = data.replace("<td ", "");

        classes = classes.substring(0, classes.indexOf(">"));

        Arrays.asList(classes.split("\\s+")).stream().forEach(
                f ->
                        parse.put(f.split("=")[0], f.split("=")[1].replace("\"", ""))
        );


        try {
            return new ObjectMapper().readTree(parse.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to map data");
        }
    };
    BiFunction<String, String, List<JsonNode>> parseOthers = (id, data) -> {

        JSONObject result = new JSONObject();
        JSONObject result2 = null;

        switch (id) {
            case "posession-home":
                result.put("data-stat", "possession");
                result.put("data-home-away", "home");
                result.put("value", data.replace("%", ""));
                break;
            case "posession-away":
                result.put("data-stat", "possession");
                result.put("data-home-away", "away");
                result.put("value", data.replace("%", ""));
                break;
            case "shots-home":
                result.put("data-stat", "shots");
                result.put("data-home-away", "home");
                result.put("value", data.substring(0, data.indexOf(" ")));
                result2 = new JSONObject();
                result2.put("data-stat", "onTarget");
                result2.put("data-home-away", "home");
                result2.put("value", data.substring(data.indexOf("(") + 1, data.indexOf(")")));
                break;
            case "shots-away":
                result.put("data-stat", "shots");
                result.put("data-home-away", "away");
                result.put("value", data.substring(0, data.indexOf(" ")));
                result2 = new JSONObject();
                result2.put("data-stat", "onTarget");
                result2.put("data-home-away", "away");
                result2.put("value", data.substring(data.indexOf("(") + 1, data.indexOf(")")));
                break;

        }

        List<JsonNode> results = new ArrayList<>();

        try {
            results.add(new ObjectMapper().readTree(result.toString()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to map data");
        }

        if (result2 != null) {
            try {
                results.add(new ObjectMapper().readTree(result2.toString()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("failed to map data");
            }
        }

        return results;

    };

    public MatchScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory
    ) {
        this.sportsScraperConfigurationFactory = sportsScraperConfigurationFactory;
    }

    @Override
    public Match scrape(Integer matchId) {

        Match match = new Match();
        match.setMatchId(matchId);
        match.setType("match");

        List<SiteRules> siteRules = sportsScraperConfigurationFactory.getConfig(
                ScraperTypeKeys.MATCHES
        ).getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules();

        SiteRules events = siteRules.stream().filter(f -> f.getId().equals("events")).findFirst().get();
        //grab the payload.
        try {

            String response = restTemplate.exchange(events.getUrl().replace("{game_id}", matchId.toString()),
                    HttpMethod.GET, null, String.class).getBody();

            Document document = Parser.htmlParser().parseInput(response, "");

            events.getRanges().forEach(
                    r ->
                    {
                        JSONObject object = new JSONObject(r);
                        int idx = object.getInt(events.getIndex());

                        IntStream.range(1, idx).forEach(
                                i -> {
                                    object.put(events.getIndex(), i);

                                    String data = ScraperUtils.compile(
                                            object,
                                            events.getXpath(),
                                            document).toString();
                                    //may have no results.
                                    if (data != null) {
                                        match.getData().add(parse.apply(data));
                                    }

                                });


                    });


            //now get the rest.
            siteRules.stream().filter(f -> !f.getId().equals("events")).
                    forEach(others -> {

                        String data = ScraperUtils.compile(
                                new JSONObject(),
                                others.getXpath(),
                                document).toString();

                        //some broken pages (based on country)
                        if (data != null) {
                            match.getData().addAll(parseOthers.apply(others.getId(), data));
                        }
                    });

        } catch (Exception e) {
            log.error("match scraper failed", e);
        }

        return match;
    }
}
