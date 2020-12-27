package com.timmytime.predictorplayerseventsreactive.request;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TensorflowPrediction {
    private FantasyEventTypes fantasyEventTypes;
    private PlayerEventOutcomeCsv playerEventOutcomeCsv;
}
