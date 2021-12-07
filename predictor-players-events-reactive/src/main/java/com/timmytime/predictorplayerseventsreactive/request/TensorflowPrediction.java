package com.timmytime.predictorplayerseventsreactive.request;

import com.timmytime.predictorplayerseventsreactive.enumerator.FantasyEventTypes;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TensorflowPrediction {
    private FantasyEventTypes fantasyEventTypes;
    private List<PlayerEventOutcomeCsv> playerEventOutcomeCsv;
}
