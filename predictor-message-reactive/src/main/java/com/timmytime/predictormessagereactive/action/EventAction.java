package com.timmytime.predictormessagereactive.action;

import com.timmytime.predictormessagereactive.model.CycleEvent;
import lombok.*;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EventAction {
    @Getter
    @Setter
    private Boolean processed;
    @Getter
    private Function<List<CycleEvent>, Boolean> handler;
}
