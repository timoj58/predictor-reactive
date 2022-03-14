package com.timmytime.predictormessagereactive.action.event;

import com.timmytime.predictormessagereactive.action.EventAction;
import com.timmytime.predictormessagereactive.enumerator.Action;
import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.model.ActionEvent;
import com.timmytime.predictormessagereactive.model.PredictorCycle;
import com.timmytime.predictormessagereactive.repo.PredictorCycleRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class Finalise implements IEventAction {

    private final PredictorCycleRepo predictorCycleRepo;

    @Override
    public Pair<Action, EventAction> create() {
        return Pair.of(
                Action.FINALISE,
                EventAction.builder()
                        .processed(Boolean.FALSE)
                        .handler((ce, ae) -> {
                            if (ce.stream().anyMatch(m -> m.getMessage().getEvent().equals(Event.STOP))) {
                                ae.add(ActionEvent.builder()
                                        .action(Action.FINALISE)
                                        .timestamp(LocalDateTime.now())
                                        .build());

                                predictorCycleRepo.save(
                                        PredictorCycle.builder()
                                                .id(UUID.randomUUID())
                                                .cycleEvents(ce)
                                                .date(LocalDateTime.now())
                                                .actionEvents(ae)
                                                .build()
                                ).subscribe();
                                return true;
                            }
                            return false;
                        }).build()
        );
    }
}
