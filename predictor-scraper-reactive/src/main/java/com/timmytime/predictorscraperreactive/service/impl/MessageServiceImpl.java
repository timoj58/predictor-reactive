package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.model.ScraperModel;
import com.timmytime.predictorscraperreactive.request.Message;
import com.timmytime.predictorscraperreactive.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final String dataHost;
    private final String teamsHost;
    private final WebClientFacade webClientFacade;

    @Autowired
    public MessageServiceImpl(
            @Value("${data.host}") String dataHost,
            @Value("${teams.host}") String teamsHost,
            WebClientFacade webClientFacade
    ){
        this.dataHost = dataHost;
        this.teamsHost = teamsHost;
        this.webClientFacade = webClientFacade;

    }

    @Override
    public void send(ScraperModel scraperModel) {
         log.info("sending message for match id {}", scraperModel.getMatchId());

         //we send it as json so
        JsonNode message = new ObjectMapper().convertValue(scraperModel, JsonNode.class);
        log.info("payload: {}", message.toString());

        webClientFacade.send(
                dataHost+"/message",
                message
        );
    }

    @Override
    public void send(Message message) {
          log.info("competition {} completed", message.getCompetition());

          webClientFacade.send(
                  teamsHost+"/message",
                  message
          );
    }

    @Override
    public void send() {
        log.info("sending scrape completed message");

        webClientFacade.send(
                dataHost+"/completed"
        );

    }

}
