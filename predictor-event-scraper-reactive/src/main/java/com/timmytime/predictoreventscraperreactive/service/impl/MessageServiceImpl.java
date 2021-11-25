package com.timmytime.predictoreventscraperreactive.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictoreventscraperreactive.enumerator.CountryCompetitions;
import com.timmytime.predictoreventscraperreactive.facade.WebClientFacade;
import com.timmytime.predictoreventscraperreactive.model.ScraperModel;
import com.timmytime.predictoreventscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.timmytime.predictoreventscraperreactive.enumerator.Providers.ESPN_ODDS;

@Slf4j
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private final String eventsDataHost;
    private final String messageHost;

    private final WebClientFacade webClientFacade;

    private final Map<String, List<String>> messages = new HashMap<>();

    @Autowired
    public MessageServiceImpl(
            @Value("${clients.event-data}") String eventsDataHost,
            @Value("${clients.message}") String messageHost,
            WebClientFacade webClientFacade
    ) {
        this.eventsDataHost = eventsDataHost;
        this.messageHost = messageHost;

        this.webClientFacade = webClientFacade;

        messages.put(ESPN_ODDS.name(), new ArrayList<>());
    }

    @Override
    public void send(String provider, String competition) {
        log.info("provider {} finished for {}", provider, competition);
        messages.get(provider).add(competition);

        CountryCompetitions countryCompetitions = CountryCompetitions.findByCompetition(competition);


        if (messages.get(provider).containsAll(countryCompetitions.getCompetitions())) {
            log.info("all competitions received, sending notification {}", countryCompetitions.name());
            try {
                JsonNode message = new ObjectMapper().readTree(
                        new JSONObject()
                                .put("type", provider)
                                .put("country", countryCompetitions.name())
                                .toString()
                );
                webClientFacade.send(
                        messageHost + "/message", message
                );

            } catch (
                    JsonProcessingException e) {
                log.error("message issue", e);
            }
        }
    }

    @Override
    public void send(ScraperModel scraperModel) {
        if (scraperModel.getData() == null) {
            log.info("failed to process, skipping likely website error");
        } else {
            JsonNode message = new ObjectMapper().convertValue(scraperModel, JsonNode.class);
            log.info("sending: {}", message.toString());

            webClientFacade.send(
                    eventsDataHost + "/message", message
            );

        }
    }
}
