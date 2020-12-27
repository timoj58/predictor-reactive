package com.timmytime.predictoreventscraperreactive.scraper.betway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import com.timmytime.predictoreventscraperreactive.scraper.IScraper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

@Slf4j
public class BetwayEventSpecificScraper implements IScraper {

    private final RestTemplate restTemplate = new RestTemplate();
    //  private final ScraperUtils scraperUtils;
    BiFunction<JSONArray, String, List<OutcomeKey>> getOutcomes = (markets, key) -> {
        List<OutcomeKey> outcomes = new ArrayList<>();

        IntStream.range(0, markets.length()).forEach(
                market -> {
                    List<Integer> outcomeKeys = new ArrayList<>();

                    JSONArray outcomeIds = markets.getJSONObject(market).getJSONArray(key);

                    IntStream.range(0, outcomeIds.length()).forEach(
                            i ->
                                    IntStream.range(0, outcomeIds.getJSONArray(i).length()).forEach(
                                            i2 -> {
                                                if (!outcomeIds.getJSONArray(i).get(i2).toString().equals("null"))
                                                    outcomeKeys.add(Integer.valueOf(outcomeIds.getJSONArray(i).get(i2).toString()));
                                            }));


                    outcomes.add(new OutcomeKey(markets.getJSONObject(market).getString("Title"), outcomeKeys));

                }

        );


        return outcomes;
    };

    @Override
    public ScraperModel scrape(BookmakerSiteRules bookmakerSiteRules, JSONObject event, String competition) {

        log.info("scraping {} {}", competition, event.getInt("eventId"));

        ScraperModel scraperModel = new ScraperModel();
        scraperModel.setCompetition(competition);
        scraperModel.setProvider(ScraperTypeKeys.BETWAY_ODDS.name());

        JSONArray results = new JSONArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject payload = new JSONObject(bookmakerSiteRules.getPayload());

        payload.put(bookmakerSiteRules.getKeys().stream().findFirst().get(), event.getInt("eventId"));

        HttpEntity<?> entity = new HttpEntity<>(payload.toString(), headers);


        JSONObject response = new JSONObject(
                restTemplate.postForEntity(bookmakerSiteRules.getUrl(), entity, String.class).getBody());

        JSONObject extractConfig = new JSONObject(bookmakerSiteRules.getExtractConfig());

        JSONObject eventObject = response.getJSONObject(extractConfig.getString("event"));
        JSONArray markets = response.getJSONArray(extractConfig.getString("markets"));
        JSONArray outcomes = response.getJSONArray(extractConfig.getString("outcomes"));

        //got some weird entries creeping in
        if (!eventObject.isNull("HomeTeamName")) {
            JSONObject result = new JSONObject();

            //grab our keys.
            IntStream.range(0, extractConfig.getJSONArray("eventValues").length()).forEach(
                    i2 -> {
                        String key = extractConfig.getJSONArray("eventValues").getJSONObject(i2).getString("key");
                        result.put(key, eventObject.get(key));
                    }
            );


            List<OutcomeKey> outcomeKeys = getOutcomes.apply(markets,
                    extractConfig.getString("marketValues"));


            JSONArray bets = new JSONArray();

            //now we have out outcome keys..lets add some outcomes to this.
            IntStream.range(0, outcomes.length()).forEach(
                    i5 -> {
                        outcomeKeys.stream().filter(
                                f -> f.getKeys().contains(outcomes.getJSONObject(i5).getInt("Id")))
                                .findFirst().ifPresent(
                                outcomeKey -> {

                                    JSONObject bet = new JSONObject();
                                    bet.put("title", outcomeKey.getTitle());

                                    IntStream.range(0, extractConfig.getJSONArray("outcomeValues").length()).forEach(
                                            i6 -> {
                                                String key = extractConfig.getJSONArray("outcomeValues").getJSONObject(i6).getString("key");
                                                bet.put(key, outcomes.getJSONObject(i5).get(key));
                                            }
                                    );

                                    bets.put(bet);
                                });
                    }
            );

            result.put("bets", bets);
            results.put(result);
        }


        try {
            scraperModel.setData(
                    new ObjectMapper().readTree(results.toString())
            );
        } catch (JsonProcessingException e) {
            log.error("failed to convert data", e);
        }


        return scraperModel;
    }

    class OutcomeKey {

        String title;
        List<Integer> keys;

        public OutcomeKey(String title, List<Integer> keys) {
            this.title = title;
            this.keys = keys;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Integer> getKeys() {
            return keys;
        }

        public void setKeys(List<Integer> keys) {
            this.keys = keys;
        }

    }

}
