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
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import us.codecraft.xsoup.XElements;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
public class LineupScraper implements IScraper<Lineup> {
    private final SportsScraperConfigurationFactory sportsScraperConfigurationFactory;
    private final RestTemplate restTemplate = new RestTemplate();
    BiFunction<String, Integer, JSONObject> createPlayer = (name, time) ->
            new JSONObject().put("name", name).put("time", time);
    Function<String, Integer> getTime = time -> {
        Matcher matcher = Pattern.compile("\\d+").matcher(time);

        if (matcher.find()) {
            return Integer.valueOf(matcher.group(0));
        }
        return 0;
    };
    //for a bug in espn
    BiFunction<JSONArray, JSONObject, Boolean> contains = (players, player) -> {
        Boolean found = Boolean.FALSE;
        for (int i = 0; i < players.length(); i++) {
            if (players.getJSONObject(i).getString("name").equals(player.getString("name"))) {
                found = Boolean.TRUE;
            }
        }

        return found;
    };
    Function<Elements, Integer> getSubTime = path -> {

        Pattern pattern = Pattern.compile("(?<=detail\">)(.*)(?=</span)");
        Matcher matcher = pattern.matcher(path.toString());

        String time = "";

        while (matcher.find()) {
            time = matcher.group(1);
        }

        return getTime.apply(time);
    };

    public LineupScraper(
            SportsScraperConfigurationFactory sportsScraperConfigurationFactory
    ) {
        this.sportsScraperConfigurationFactory = sportsScraperConfigurationFactory;
    }

    @Override
    public Lineup scrape(Integer matchId) throws JsonProcessingException {

        List<SiteRules> siteRules
                = sportsScraperConfigurationFactory.getConfig(
                ScraperTypeKeys.LINEUPS
        ).getSportScrapers()
                .stream()
                .findFirst()
                .get()
                .getSiteRules();

        Lineup lineup = new Lineup();
        lineup.setMatchId(matchId);

        SiteRules rules =
                siteRules
                        .stream()
                        .filter(f -> f.getId().equals("events"))
                        .findFirst()
                        .get();

        JSONObject results = new JSONObject();

        String url = rules.getUrl().replace("{game_id}", matchId.toString());

        log.info("url is " + url);
        //grab the payload.
        String response = restTemplate.exchange(url,
                HttpMethod.GET, null, String.class).getBody();


        Document document = Parser.htmlParser().parseInput(response, "");

        rules.getRanges().forEach(
                r -> {
                    JSONObject object = new JSONObject(r);
                    int idx = object.getInt(rules.getIndex());

                    //home team is first.
                    IntStream.range(1, object.getInt("divIdx")).forEach(
                            team -> {
                                JSONArray teamPlayers = new JSONArray();
                                JSONArray teamPlayingSubs = new JSONArray();
                                JSONArray teamNonPlayingSubs = new JSONArray();


                                IntStream.range(1, idx).forEach(
                                        players -> {
                                            object.put(rules.getIndex(), players);
                                            object.put("divIdx", team);
                                            object.put("tbodyIdx", 1);
                                            object.put("subIdx", "");
                                            object.put("spanIdx", 3);

                                            XElements player = ScraperUtils.compile(object, rules.getXpath(), document);


                                            Integer subTime = 90;
                                            //playing subs.
                                            object.put("subIdx", "[2]");

                                            XElements playingSub = ScraperUtils.compile(object, rules.getXpath(), document);

                                            if (playingSub.get() != null) {

                                                //time
                                                object.put("spanIdx", 2);
                                                XElements time = ScraperUtils.compile(object, rules.getXpath().replace("/a/text()", "/text()"), document);

                                                subTime = getSubTime.apply(time.getElements());

                                                teamPlayingSubs.put(createPlayer.apply(
                                                        ScraperUtils.format.apply(playingSub.get()),
                                                        90 - subTime)
                                                        .put("index", players));
                                            }

                                            if (player.get() != null) {

                                                JSONObject created = createPlayer.apply(
                                                        ScraperUtils.format.apply(player.get()),
                                                        subTime)
                                                        .put("index", players);

                                                if (!contains.apply(teamPlayers, created)) {
                                                    teamPlayers.put(created);
                                                }
                                            }


                                        });

                                //subs
                                IntStream.range(1, 8).forEach(
                                        players -> {
                                            object.put(rules.getIndex(), players);
                                            object.put("divIdx", team);
                                            object.put("tbodyIdx", 2);
                                            object.put("subIdx", "");
                                            object.put("spanIdx", 3);

                                            XElements player = ScraperUtils.compile(object, rules.getXpath(), document);
                                            if (player.get() != null) {
                                                teamNonPlayingSubs.put(createPlayer.apply(
                                                        ScraperUtils.format.apply(player.get()),
                                                        0));
                                            }
                                        });

                                results.put(team == 1 ? "home" : "away", teamPlayers);
                                results.put(team == 1 ? "homePlayingSubs" : "awayPlayingSubs", teamPlayingSubs);
                                results.put(team == 1 ? "homeNonPlayingSubs" : "awayNonPlayingSubs", teamNonPlayingSubs);
                            });
                });


        lineup.setData(new ObjectMapper().readTree(results.toString()));
        lineup.setType("lineup");
        return new PlayerStatsScraper(sportsScraperConfigurationFactory, lineup).scrape(matchId);
    }
}
