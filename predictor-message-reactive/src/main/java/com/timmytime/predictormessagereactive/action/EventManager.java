package com.timmytime.predictormessagereactive.action;

import com.timmytime.predictormessagereactive.action.event.IEventAction;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.service.InitService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class EventManager {
    private final Map<Action, EventAction> eventManager = new ConcurrentHashMap<>();
    private final Collection<IEventAction> eventActions;

    public void init() {

        eventActions.forEach(impl -> {
            var created = impl.create();
            eventManager.put(created.getLeft(), created.getRight());
        });


    }

    public EventAction get(Action action) {
        return eventManager.get(action);
    }

}
