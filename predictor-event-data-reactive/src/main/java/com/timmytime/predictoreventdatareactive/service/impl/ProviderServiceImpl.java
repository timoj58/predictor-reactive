package com.timmytime.predictoreventdatareactive.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoreventdatareactive.enumerator.Providers;
import com.timmytime.predictoreventdatareactive.service.BetwayService;
import com.timmytime.predictoreventdatareactive.service.PaddyPowerService;
import com.timmytime.predictoreventdatareactive.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
@Service("providerService")
public class ProviderServiceImpl implements ProviderService {

    private Consumer<Pair<Providers, JsonNode>> receiveJson;
    private Consumer<JSONObject> receiveEvent;
    private Consumer<Pair<JSONObject, Consumer<JSONObject>>> betsReceived;


    @Autowired
    public ProviderServiceImpl(
            BetwayService betwayService,
            PaddyPowerService paddyPowerService
    ) {
        Flux<Pair<Providers, JsonNode>> messages = Flux.push(sink ->
                ProviderServiceImpl.this.receiveJson = sink::next, FluxSink.OverflowStrategy.BUFFER);

        Flux<JSONObject> events = Flux.push(sink ->
                ProviderServiceImpl.this.receiveEvent = sink::next, FluxSink.OverflowStrategy.BUFFER);

        Flux<Pair<JSONObject, Consumer<JSONObject>>> bets = Flux.push(sink ->
                ProviderServiceImpl.this.betsReceived = sink::next, FluxSink.OverflowStrategy.BUFFER);

        messages.limitRate(1).subscribe(msg -> process(msg).forEach(receiveEvent));
        events.limitRate(1).subscribe(event -> {
            switch (Providers.valueOf(event.getString("provider"))) {
                case PADDYPOWER_ODDS:
                    processBets(paddyPowerService.prepare(event));
                    break;
                case BETWAY_ODDS:
                    processBets(betwayService.prepare(event));
                    break;
            }
        });

        bets.limitRate(1)
                .subscribe(bet -> bet.getRight().accept(bet.getLeft()));
    }

    @Override
    public void receive(Pair<Providers, JsonNode> message) {
        receiveJson.accept(message);
    }

    private List<JSONObject> process(Pair<Providers, JsonNode> message) {
        JSONObject details = new JSONObject(message.getRight().toString());

        List<JSONObject> events = new ArrayList<>();

        JSONArray data = details.getJSONArray("data");

        IntStream.range(0, data.length()).forEach(i ->
                events.add(data.getJSONObject(i)
                        .put("competition", details.getString("competition"))
                        .put("provider", message.getLeft().name())));

        return events;
    }

    private void processBets(Pair<List<JSONObject>, Consumer<JSONObject>> preparedBets) {
        preparedBets.getLeft().forEach(bet -> betsReceived.accept(Pair.of(bet, preparedBets.getRight())));
    }

}
