package com.timmytime.predictoreventsreactive.request;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TensorflowPrediction {

    private Predictions predictions;
    private Prediction prediction;
    private String country;
}
