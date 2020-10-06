package com.timmytime.predictorplayersreactive.request;

import com.timmytime.predictorplayersreactive.enumerator.FantasyEventTypes;
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
