package com.timmytime.predictorscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.Lineup;
import com.timmytime.predictorscraperreactive.util.ScraperUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class PlayerStatsScraper implements IScraper<Lineup> {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory;
    private final RestTemplate restTemplate = new RestTemplate();

    private final Lineup lineup;


    public PlayerStatsScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory,
            Lineup lineup
    ) {
        this.sportsScraperConfigurationFactory = sportsScraperConfigurationFactory;
        this.lineup = lineup;
    }


    @Override
    public Lineup scrape(Integer matchId) throws JsonProcessingException {

        JSONObject lineupData = new JSONObject(lineup.getData().toString());

        List<SiteRules> siteRules
                = sportsScraperConfigurationFactory.getConfig(ScraperTypeKeys.PLAYER_STATS)
                .getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules();


        SiteRules events = siteRules.stream().filter(f -> f.getId().equals("events")).findFirst().get();

        //grab the payload.
        String response = restTemplate.exchange(events.getUrl().replace("{game_id}", matchId.toString()),
                HttpMethod.GET, null, String.class).getBody();

        Document document = Parser.htmlParser().parseInput(response, "");


        events.getRanges().forEach(
                eventsRange ->
                {
                    int eventMax = new JSONObject(eventsRange).getInt(events.getIndex());

                    IntStream.range(1, eventMax).forEach(
                            eventIdx -> {
                                SiteRules players = siteRules.stream().filter(f -> f.getId().equals("players")).findFirst().get();
                                players.getRanges().forEach(
                                        playerRange ->
                                        {
                                            int playerMax = new JSONObject(playerRange).getInt(players.getIndex());

                                            IntStream.range(1, playerMax).forEach(
                                                    playerIdx -> {

                                                        siteRules.stream().filter(f -> !f.getId().equals("players") && !f.getId().equals("events"))
                                                                .forEach(stat -> {

                                                                    String xpath = stat.getXpath().replace("{sub}", "div");

                                                                    JSONObject object = new JSONObject();

                                                                    object.put(events.getIndex(), eventIdx);
                                                                    object.put(players.getIndex(), playerIdx);

                                                                    if (stat.getIndex().trim().isEmpty()
                                                                            || stat.getIndex().equals(String.valueOf(playerIdx))
                                                                            || (stat.getIndex().equals("greater") && playerIdx >= stat.getOccurs())) {


                                                                        String data = ScraperUtils.compile(
                                                                                object,
                                                                                xpath,
                                                                                document).toString();
                                                                        //may have no results.
                                                                        if (data != null) {
                                                                            //        log.info("data is {} {}", stat.getId(), data);

                                                                            Integer index;
                                                                            if (eventIdx == 1) {
                                                                                index = findIndex(lineupData.getJSONArray("home"), playerIdx);
                                                                                if (index != -1) {
                                                                                    lineupData.getJSONArray("home").getJSONObject(index).put(stat.getId(), data);
                                                                                }
                                                                            } else {
                                                                                index = findIndex(lineupData.getJSONArray("away"), playerIdx);
                                                                                if (index != -1) {
                                                                                    lineupData.getJSONArray("away").getJSONObject(index).put(stat.getId(), data);
                                                                                }
                                                                            }
                                                                            //  results.add(parse.apply(data));
                                                                        }

                                                                        //now check for any subs.
                                                                        xpath = stat.getXpath().replace("{sub}", "div[2]");


                                                                        data = ScraperUtils.compile(
                                                                                object,
                                                                                xpath,
                                                                                document).toString();
                                                                        //may have no results.
                                                                        if (data != null) {
                                                                            //        log.info("sub data is {} {}", stat.getId(), data);
                                                                            Integer index;
                                                                            if (eventIdx == 1) {
                                                                                index = findIndex(lineupData.getJSONArray("homePlayingSubs"), playerIdx);
                                                                                if (index != -1) {
                                                                                    lineupData.getJSONArray("homePlayingSubs").getJSONObject(index).put(stat.getId(), data);
                                                                                }
                                                                            } else {
                                                                                index = findIndex(lineupData.getJSONArray("awayPlayingSubs"), playerIdx);
                                                                                if (index != -1) {
                                                                                    lineupData.getJSONArray("awayPlayingSubs").getJSONObject(index).put(stat.getId(), data);
                                                                                }
                                                                            }
                                                                        }
                                                                    }


                                                                });
                                                    });
                                        });
                            });
                });

        log.info("we have {}", lineup.getData().toString());
        lineup.setData(new ObjectMapper().readTree(lineupData.toString()));

        return lineup;

    }


    private Integer findIndex(JSONArray players, Integer index) {
        for (int i = 0; i < players.length(); i++) {
            if (players.getJSONObject(i).getInt("index") == index) {
                return i;
            }
        }

        return -1;
    }
}
