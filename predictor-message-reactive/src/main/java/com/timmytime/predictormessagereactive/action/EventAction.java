package com.timmytime.predictormessagereactive.action;

import com.timmytime.predictormessagereactive.model.ActionEvent;
import com.timmytime.predictormessagereactive.model.CycleEvent;
import lombok.*;

import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EventAction {
    @Getter
    @Setter
    private Boolean processed;
    @Getter
    private BiFunction<List<CycleEvent>, List<ActionEvent>, Boolean> handler;
}
