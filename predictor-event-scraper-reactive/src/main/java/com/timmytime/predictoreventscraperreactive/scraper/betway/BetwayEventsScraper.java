package com.timmytime.predictoreventscraperreactive.scraper.betway;

import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class BetwayEventsScraper {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Integer> scrape(BookmakerSiteRules rules, BookmakerSiteRules league) {

        log.info("scraping {}", league.getId());

        List<Integer> results = new ArrayList<>();

        JSONObject payload = new JSONObject(rules.getPayload());
        JSONObject extractConfig = new JSONObject(rules.getExtractConfig());

        List<JSONObject> leagueKeys = league.getKeys()
                .stream()
                .map(m -> new JSONObject(m))
                .collect(Collectors.toList());

        rules.getKeys().stream().forEach(
                key ->
                        payload.put(key, leagueKeys.stream().filter(f -> f.has(key)).findFirst().get().getString(key))

        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(payload.toString(), headers);


        JSONObject response = new JSONObject(
                restTemplate.postForEntity(rules.getUrl(), entity, String.class).getBody());

        IntStream.range(0, response.getJSONArray(extractConfig.getString("list")).length()).forEach(
                i -> results.add(response.getJSONArray(extractConfig.getString("list")).getJSONObject(i).getInt(extractConfig.getString("key")))
        );


        return results;
    }


}
