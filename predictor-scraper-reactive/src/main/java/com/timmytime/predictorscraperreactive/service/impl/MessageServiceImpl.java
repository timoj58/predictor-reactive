package com.timmytime.predictorscraperreactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.facade.WebClientFacade;
import com.timmytime.predictorscraperreactive.model.ScraperModel;
import com.timmytime.predictorscraperreactive.request.Message;
import com.timmytime.predictorscraperreactive.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
    public Integer send(ScraperModel scraperModel) {
        //we send it as json so
        JsonNode message = new ObjectMapper().convertValue(scraperModel, JsonNode.class);

        webClientFacade.send(
                dataHost + "/message",
                message
        );

        return scraperModel.getMatchId();
    }

    @Override
    public void send() {

        log.info("sending scrape completed message");

        Flux.fromArray(CompetitionFixtureCodes.values())
                .doOnNext(competition -> send(new Message(competition.name().toLowerCase())))
                .doFinally(close -> {
                    webClientFacade.send(
                            dataHost + "/completed"
                    );
                })
                .subscribe();

    }

    private void send(Message message) {
        log.info("competition {} completed", message.getCompetition());

        webClientFacade.send(
                teamHost + "/message",
                message
        );
    }

}
