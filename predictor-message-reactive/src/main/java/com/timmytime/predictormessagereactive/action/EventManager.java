package com.timmytime.predictormessagereactive.action;

import com.timmytime.predictormessagereactive.action.event.IEventAction;
import com.timmytime.predictormessagereactive.enumerator.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventManager {
    private final Map<Action, EventAction> eventManager = new ConcurrentHashMap<>();

    @Autowired
    public EventManager(
            Collection<IEventAction> eventActions
    ) {
        eventActions.forEach(impl -> {
            var created = impl.create();
            eventManager.put(created.getLeft(), created.getRight());
        });

    }

    public EventAction get(Action action) {
        return eventManager.get(action);
    }

}
