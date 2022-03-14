package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.enumerator.Action;
import org.apache.commons.lang3.tuple.Pair;

public interface IEventAction {
    Pair<Action, EventAction> create();
}
