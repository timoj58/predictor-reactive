package com.timmytime.predictorscraperreactive.scraper;

import com.timmytime.predictorscraperreactive.configuration.SiteRules;
import com.timmytime.predictorscraperreactive.enumerator.ScraperTypeKeys;
import com.timmytime.predictorscraperreactive.factory.SportsScraperConfigurationFactory;
import com.timmytime.predictorscraperreactive.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

@Slf4j
public class ResultScraper {

    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory;
    private final RestTemplate restTemplate = new RestTemplate();

    public ResultScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory
    ) {
        this.sportsScraperConfigurationFactory = sportsScraperConfigurationFactory;
    }

    public List<Result> scrape(SiteRules competition, LocalDate date) {

        log.info("date is {}", date.toString());

        SiteRules eventRules
                = sportsScraperConfigurationFactory
                .getConfig(ScraperTypeKeys.RESULTS)
                .getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules()
                .stream()
                .filter(f -> f.getId().equals("generic"))
                .findFirst()
                .get();

        String url = competition.getUrl().replace("{date}", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).replace("-", ""));

        log.info("url {}", url);

        List<Result> results = new ArrayList<>();

        try {
            results = process(url, eventRules, competition);

        } catch (Exception e) {
            log.error("first fail, trying again", e);
            try {
                results = process(url, eventRules, competition);
            } catch (Exception e2) {
                log.error("give up", e2);
            }
        }

        return results;
    }


    BiFunction<JSONArray, JSONArray, JSONArray> iterate = (paths, data) -> {

        JSONArray results = new JSONArray();

        IntStream.range(0, data.length()).forEach(d -> {

                    JSONObject result = new JSONObject();

                    IntStream.range(0, paths.length()).forEach(
                            i -> {
                                JSONObject path = paths.getJSONObject(i);

                                JSONObject object = path.getString("objectType").equals("array") ?
                                        data.getJSONObject(d).has(path.getString("objectKey")) ?
                                                data.getJSONObject(d).getJSONArray(path.getString("objectKey")).getJSONObject(path.getInt("index"))
                                                : null
                                        : data.getJSONObject(d).getJSONObject(path.getString("objectKey"));

                                if (object != null) {
                                    result.put(path.getString("dataKey"), object.get(path.getString("dataKey")).toString());
                                } else {
                                    log.info("we have no key for " + data.getJSONObject(d).toString());
                                }
                            });
                    results.put(result);
                }

        );

        return results;
    };


    BiFunction<JSONArray, JSONObject, Object> extract = (config, event) -> {


        for (int i = 0; i < config.length(); i++) {

            JSONObject temp = new JSONObject(config.getJSONObject(i).toString());

            String key = temp.getString("key");
            String index = temp.has("index") ? temp.getString("index") : "";
            Boolean hasValue = temp.has("value");

            switch (temp.getString("type")) {
                case "array":
                    if (index.equals("iterate")) {
                        //we simply..process object / data item. as need both.
                        return iterate.apply(temp.getJSONArray("data"), event.getJSONArray(key));
                    }
                    event = hasValue ? filter(event.getJSONArray(key), index, temp.getString("value")) : event.getJSONArray(key).getJSONObject(Integer.valueOf(index));
                    break;
                case "object":
                    event = event.getJSONObject(key);
                    break;
                default:
                    return event.getString(key);

            }

        }

        return "";
    };


    private JSONObject filter(JSONArray array, String index, String value) {
        for (int i = 0; i < array.length(); i++) {
            if (array.getJSONObject(i).getString(index).equals(value)) {
                return array.getJSONObject(i);
            }
        }
        return null;
    }

    private List<Result> process(String url, SiteRules eventRules, SiteRules competition) {
        List<Result> results = new ArrayList<>();
        //grab the payload.
        String response = restTemplate.exchange(url,
                HttpMethod.GET, null, String.class).getBody();

        Document document = Parser.htmlParser().parseInput(response, "");


        document.head().getAllElements().stream().filter(f -> f.tag().toString().equals("script"))
                .filter(f -> f.data().contains(eventRules.getXpath())).findFirst().ifPresent(
                c -> {

                    JSONObject object = new JSONObject(c.data().substring(c.data().indexOf("{"))
                            .replace("<script>", "")
                            .replace("</script>", ""));

                    JSONArray events = object.getJSONArray(eventRules.getIndex());


                    JSONObject keys = new JSONObject(
                            eventRules.getRanges().stream().findFirst().get());

                    String key = keys.getString("key");
                    JSONArray eventDate = keys.getJSONArray("date");
                    JSONArray homeScore = keys.getJSONArray("homeScore");
                    JSONArray homeTeam = keys.getJSONArray("homeTeam");
                    JSONArray awayScore = keys.getJSONArray("awayScore");
                    JSONArray awayTeam = keys.getJSONArray("awayTeam");
                    JSONArray details = keys.getJSONArray("details");

                    /*
                    player events are also in this object.
                     */

                    IntStream.range(0, events.length()).forEach(
                            f -> {
                                JSONObject event = events.getJSONObject(f);

                                Result result = new Result();

                                result.setMatchId(event.getInt(key));
                                result.setType("result");

                                result.setHomeScore(Integer.valueOf(extract.apply(homeScore, event).toString()));
                                result.setAwayScore(Integer.valueOf(extract.apply(awayScore, event).toString()));
                                result.setDate(extract.apply(eventDate, event).toString());
                                result.setHomeTeam(extract.apply(homeTeam, event).toString().trim());
                                result.setAwayTeam(extract.apply(awayTeam, event).toString().trim());
                                result.setDetails(extract.apply(details, event).toString());
                                result.setCompetition(competition.getId());
                                result.setCountry(competition.getId().substring(0, competition.getId().indexOf("_")));

                                results.add(result);
                            });
                }
        );

        return results;
    }
}
