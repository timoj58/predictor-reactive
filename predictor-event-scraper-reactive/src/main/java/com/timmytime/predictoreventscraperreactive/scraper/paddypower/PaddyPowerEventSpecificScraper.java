package com.timmytime.predictoreventscraperreactive.scraper.paddypower;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventscraperreactive.configuration.BookmakerSiteRules;
import com.timmytime.predictoreventscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictoreventscraperreactive.facade.ScraperProxyFacade;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import com.timmytime.predictoreventscraperreactive.request.ScraperRequest;
import com.timmytime.predictoreventscraperreactive.scraper.IScraper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class PaddyPowerEventSpecificScraper implements IScraper {

    private static final Logger log = LoggerFactory.getLogger(PaddyPowerEventSpecificScraper.class);

    private final ScraperProxyFacade scraperProxyFacade;

    public PaddyPowerEventSpecificScraper(
            ScraperProxyFacade scraperProxyFacade
    ){
        this.scraperProxyFacade = scraperProxyFacade;
    }

    @Override
    public ScraperModel scrape(BookmakerSiteRules bookmakerSiteRules, JSONObject event,  String competition) {
        ScraperModel scraperModel = new ScraperModel();
        scraperModel.setProvider(ScraperTypeKeys.PADDYPOWER_ODDS.name());
        scraperModel.setCompetition(competition);

        log.info("getting event for {}", competition);
        JSONArray events = new JSONArray();

        try {

            JSONObject facets = event.getJSONArray("facets").getJSONObject(0);

            IntStream.range(0, facets.getInt("totalResults")).forEach(
                    i -> {
                        String url = bookmakerSiteRules.getUrl();

                        for (String s : bookmakerSiteRules.getKeys()) {
                            JSONObject key = new JSONObject(s);

                            log.info(event.toString());

                            url = urlReplace(key, key.has("paths") ? facets : event, i, url);
                        }

                        log.info("url " + url);
                        //sort this mess out!

                                extractEvent(
                                        new JSONObject(
                                                scraperProxyFacade
                                                        .scrape(
                                                                "response",
                                                                "get",
                                                                new ScraperRequest(url, null))
                                                        .getResponse().toString())
                                        , bookmakerSiteRules.getExtractConfig())
                                .stream()
                                .forEach(e -> events.put(e));
                    }
            );

        } catch (Exception e) {
            log.error("failed for event", e);
            return new ScraperModel();
        }

        try {
            scraperModel.setData(
                    new ObjectMapper().readTree(events.toString())
            );
        } catch (JsonProcessingException e) {
            log.error("failed to convert event", e);
        }

        return scraperModel;
    }

    private String urlReplace(JSONObject key, JSONObject competition, Integer index, String url) {
        if (key.has("paths")) {
            JSONArray paths = key.getJSONArray("paths");

            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder result = new StringBuilder();

            IntStream.range(0, paths.length()).forEach(
                    i -> {
                        JSONObject path = new JSONObject(paths.getJSONObject(i).toString());

                        if (path.getString("type").equals("array")) {
                            stringBuilder.append(competition.getJSONArray(path.getString("key")).getJSONObject(index));
                        } else if (path.getString("type").equals("object")) {

                            result.append(url.replace(
                                    key.getString("replace"),
                                    new JSONObject(stringBuilder.toString())
                                            .getJSONObject(path.getString("key"))
                                            .get(key.getString("key")).toString()));
                        }
                    }
            );

            return result.toString();
        } else {
            return url.replace(key.getString("replace"), competition.getString(key.getString("key")));
        }
    }

    private List<JSONObject> extractEvent(JSONObject event, String extractConfig){
        List<JSONObject> responses = new ArrayList<>();

         /*
          break open config.  so its one.  get our object.
         */
        JSONObject config = new JSONObject(extractConfig);

        JSONObject object = event.getJSONObject(config.getString("key")).getJSONObject(config.getString("path"));

        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {

            JSONObject eventObject = new JSONObject();

            String key = keys.next();

            if (config.has("eventId")) {
                eventObject.put(config.getString("eventId"), object.getJSONObject(key).get(config.getString("eventId")));
            }
            if (config.has("eventTime")) {
                eventObject.put(config.getString("eventTime"), object.getJSONObject(key).getString(config.getString("eventTime")));
            }
            if (config.has("type")) {
                eventObject.put(config.getString("type"), object.getJSONObject(key).getString(config.getString("type")));
            }

            JSONArray results = object.getJSONObject(key).getJSONArray(config.getString("result"));

            JSONArray paths = config.getJSONArray("values");

            JSONArray outcomes = new JSONArray();

            IntStream.range(0, results.length()).forEach(result -> {

                        if (results.getJSONObject(result).getJSONObject("result").keys().hasNext()) {

                            JSONObject response = new JSONObject();

                            IntStream.range(0, paths.length()).forEach(
                                    i -> {
                            /*
                              for moment its football.  so get the tree events.
                             */

                                        JSONObject path = paths.getJSONObject(i);
                                        JSONObject resultObject = new JSONObject().put("result", results.getJSONObject(result));


                                        String finalKey = "";
                                        if (path.getString("key").contains(":")) {
                                            List<String> delimPath = Arrays.asList(path.getString("key").split(":"));

                                            IntStream.range(0, delimPath.size() - 1).forEach(
                                                    delim -> {
                                                        resultObject.put("result", resultObject.getJSONObject("result").getJSONObject(delimPath.get(delim)));
                                                    }
                                            );

                                            finalKey = delimPath.get(delimPath.size() - 1);
                                        } else {
                                            finalKey = path.getString("key");
                                        }

                                        response.put(finalKey, resultObject.getJSONObject("result").get(finalKey));

                                    }

                            );

                            outcomes.put(response);
                        }
                    }
            );

            //add results here.
            eventObject.put("outcomes", outcomes);

            responses.add(new JSONObject().put("results", eventObject));
        }

        return responses;
    }
}
