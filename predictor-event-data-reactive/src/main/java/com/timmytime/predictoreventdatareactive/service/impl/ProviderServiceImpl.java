package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.service.EspnService;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service("providerService")
public class ProviderServiceImpl implements ProviderService {

    private Consumer<Pair<Providers, JsonNode>> receiveJson;
    private Consumer<JSONObject> receiveEvent;
    private Consumer<Pair<JSONObject, Consumer<JSONObject>>> betsReceived;


    @Autowired
    public ProviderServiceImpl(
            EspnService espnService
    ) {
        Flux<Pair<Providers, JsonNode>> messages = Flux.create(sink ->
                ProviderServiceImpl.this.receiveJson = sink::next, FluxSink.OverflowStrategy.BUFFER);

        Flux<JSONObject> events = Flux.create(sink ->
                ProviderServiceImpl.this.receiveEvent = sink::next, FluxSink.OverflowStrategy.BUFFER);

        Flux<Pair<JSONObject, Consumer<JSONObject>>> bets = Flux.create(sink ->
                ProviderServiceImpl.this.betsReceived = sink::next, FluxSink.OverflowStrategy.BUFFER);

        messages.limitRate(10).subscribe(msg -> process(msg).forEach(receiveEvent));
        events.limitRate(10)
                .subscribe(event -> {
            switch (Providers.valueOf(event.getString("provider"))) {
                case ESPN_ODDS:
                    processBets(espnService.prepareWrapper(event));
                    break;
            }
        });

        bets.limitRate(10)
                .subscribe(bet -> bet.getRight().accept(bet.getLeft()));
    }

    @Override
    public void receive(Pair<Providers, JsonNode> message) {
        receiveJson.accept(message);
    }

    private List<JSONObject> process(Pair<Providers, JsonNode> message) {
        JSONObject details = new JSONObject(message.getRight().toString());

        List<JSONObject> events = new ArrayList<>();  //TODO refactor this all.  legacy now one for 1.  ie 1 event.

        events.add(
                details
                        .put("competition", details.getString("competition"))
                        .put("provider", message.getLeft().name()
                        )
        );
        return events;
    }

    private void processBets(Pair<List<JSONObject>, Consumer<JSONObject>> preparedBets) {
        preparedBets.getLeft().forEach(bet -> betsReceived.accept(Pair.of(bet, preparedBets.getRight())));
    }

}
