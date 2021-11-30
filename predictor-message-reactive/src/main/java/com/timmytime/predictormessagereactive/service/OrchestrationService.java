package com.timmytime.predictormessagereactive.service;

import com.timmytime.predictormessagereactive.model.CycleEvent;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface OrchestrationService {
    void process(CycleEvent cycleEvent);
    Mono<Boolean> testStatus(@PathVariable String action);
}
