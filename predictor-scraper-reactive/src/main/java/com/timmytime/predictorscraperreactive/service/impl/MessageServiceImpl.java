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
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private final String dataHost;
    private final String teamHost;
    private final WebClientFacade webClientFacade;
    private final List<CompetitionFixtureCodes> sent = new ArrayList<>();
    private final AtomicInteger messageSentCounter = new AtomicInteger(0);
    private Consumer<JsonNode> messageConsumer;

    @Autowired
    public MessageServiceImpl(
            @Value("${clients.data}") String dataHost,
            @Value("${clients.team}") String teamHost,
            WebClientFacade webClientFacade
    ) {
        this.dataHost = dataHost;
        this.teamHost = teamHost;
        this.webClientFacade = webClientFacade;


        Flux<JsonNode> messageQueue = Flux.push(sink ->
                MessageServiceImpl.this.messageConsumer = sink::next, FluxSink.OverflowStrategy.BUFFER);

        messageQueue.limitRate(10).subscribe(this::sendMessage);
    }

    @Override
    public Integer send(ScraperModel scraperModel) {
        //we send it as json so
        JsonNode message = new ObjectMapper().convertValue(scraperModel, JsonNode.class);
        messageConsumer.accept(message);

        return scraperModel.getMatchId();
    }

    @Override
    public void send(Message message) {
        log.info("competition {} completed", message.getCompetition());
        Mono.just(
                CompetitionFixtureCodes.valueOf(message.getCompetition().toUpperCase())
        )
                .doOnNext(sent::add)
                .doFinally(send ->
                        Mono.just(message)
                                .doOnNext(msg -> webClientFacade.send(teamHost + "/message", msg))
                                .doFinally(finish -> {
                                    if (sent.containsAll(Arrays.asList(CompetitionFixtureCodes.values()))) {
                                        send();
                                    }
                                })
                                .subscribe()
                )
                .subscribe();

    }

    @Override
    public Integer getMessagesSentCount() {
        return messageSentCounter.get();
    }

    private void send() {
        log.info("sending scrape completed message");
        webClientFacade.send(dataHost + "/completed");
    }

    private void sendMessage(JsonNode message) {
        messageSentCounter.incrementAndGet();
        webClientFacade.send(dataHost + "/message", message);
    }

}
