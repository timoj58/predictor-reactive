package com.timmytime.predictormessagereactive.action.handler;

import com.timmytime.predictormessagereactive.enumerator.EventType;

import java.util.List;

public class TrainingHandler {
    public Boolean training(List<EventType> events, Runnable train) {
        if (events.containsAll(EventType.competitions())
        ) {
            train.run();
            return true;
        }
        return false;

    }

}
