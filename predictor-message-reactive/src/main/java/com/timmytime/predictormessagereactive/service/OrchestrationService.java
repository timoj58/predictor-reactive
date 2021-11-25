package com.timmytime.predictormessagereactive.service;

import com.timmytime.predictormessagereactive.model.CycleEvent;

public interface OrchestrationService {
    void process(CycleEvent cycleEvent);
}
