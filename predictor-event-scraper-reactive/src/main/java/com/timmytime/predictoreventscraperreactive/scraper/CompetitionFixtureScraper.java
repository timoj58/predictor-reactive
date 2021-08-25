package com.timmytime.predictoreventscraperreactive.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventscraperreactive.configuration.CompetitionFixtures;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CompetitionFixtureScraper {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MessageService messageService;

    @Autowired
    public CompetitionFixtureScraper(
            MessageService messageService
    ){
        this.messageService = messageService;
    }

    public void scrape(CompetitionFixtures competitionFixtures){

        log.info("scraping {}", competitionFixtures.getUrl());

        String response = restTemplate.exchange(competitionFixtures.getUrl(),
                HttpMethod.GET, null, String.class).getBody();

        Document document = Parser.htmlParser().parseInput(response, "");

        Flux.fromStream(document.select(".schedule tbody tr").stream())
                .doOnNext(row -> {
                    JSONObject result = new JSONObject();
                    var home = row.child(0).select("a.team-name span").text();
                    var away = row.child(1).select("a.team-name span").text();
                    var date = row.child(2).attr("data-date");

                    result.put("home", home);
                    result.put("away", away);
                    result.put("milliseconds", LocalDateTime.parse(
                            date,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'")).toEpochSecond(OffsetDateTime.now().getOffset())
                    );

                    log.info("{} v {} on {}", home, away, result.get("milliseconds"));

                    messageService.send(
                            ScraperModel.builder()
                                    .provider("ESPN_ODDS")
                                    .competition(competitionFixtures.getCode().name().toLowerCase())
                                    .data(convert(result))
                                    .build());

                })
        .doFinally(finish ->
                Mono.just(competitionFixtures.getCode())
                        .delayElement(Duration.ofSeconds(5))
                        .subscribe(notify -> messageService.send("ESPN_ODDS", notify.name().toLowerCase()))
        ).subscribe();

    }

    private JsonNode convert(JSONObject result){
        try {
            return new ObjectMapper().readTree(result.toString());
        } catch (JsonProcessingException e) {
            log.error("failed to convert data", e);
            return null;
        }
    }
}
