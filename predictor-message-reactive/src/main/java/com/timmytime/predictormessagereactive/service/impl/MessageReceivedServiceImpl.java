package com.timmytime.predictormessagereactive.service.impl;

import com.timmytime.predictormessagereactive.model.CycleEvent;
import com.timmytime.predictormessagereactive.request.Message;
import com.timmytime.predictormessagereactive.service.MessageReceivedService;
import com.timmytime.predictormessagereactive.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Service
public class MessageReceivedServiceImpl implements MessageReceivedService {

    private final OrchestrationService orchestrationService;
    private Consumer<Message> consumer;

    @Autowired
    public MessageReceivedServiceImpl(
            OrchestrationService orchestrationService
    ){
        this.orchestrationService = orchestrationService;

        Flux<Message> receiver = Flux.push(sink -> consumer = sink::next, FluxSink.OverflowStrategy.BUFFER);
        receiver.limitRate(1).subscribe(this::process);

    }

    @Override
    public Mono<Void> receive(Mono<Message> message) {
        return message.doOnNext(msg -> consumer.accept(msg))
                .thenEmpty(Mono.empty());
    }

    private void process(Message message){
        orchestrationService.process(new CycleEvent(message));
    }
}