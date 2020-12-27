package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.model.ScraperModel;
import com.timmytime.predictorscraperreactive.request.Message;
import com.timmytime.predictorscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private final String dataHost;
    private final String teamHost;
    private final WebClientFacade webClientFacade;

    @Autowired
    public MessageServiceImpl(
            @Value("${clients.data}") String dataHost,
            @Value("${clients.team}") String teamHost,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.teamHost = teamHost;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void send(ScraperModel scraperModel) {
        log.info("sending message for match id {}", scraperModel.getMatchId());

        //we send it as json so
        JsonNode message = new ObjectMapper().convertValue(scraperModel, JsonNode.class);
        log.info("payload: {}", message.toString());

        webClientFacade.send(
                dataHost + "/message",
                message
        );
    }

    @Override
    public void send(Message message) {
        log.info("competition {} completed", message.getCompetition());

        webClientFacade.send(
                teamHost + "/message",
                message
        );
    }

    @Override
    public void send() {
        log.info("sending scrape completed message");

        webClientFacade.send(
                dataHost + "/completed"
        );

    }

}
