package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.enumerator.Predictions;
import com.timmytime.predictoreventsreactive.facade.WebClientFacade;
import com.timmytime.predictoreventsreactive.request.Prediction;
import com.timmytime.predictoreventsreactive.request.TensorflowPrediction;
import com.timmytime.predictoreventsreactive.service.TensorflowPredictionService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class TensorflowPredictionServiceImplTest {

    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);


    private final TensorflowPredictionService tensorflowPredictionService
            = new TensorflowPredictionServiceImpl("training", "results/<country>/<receipt>", "goals", webClientFacade);

    @Test
    void predict() throws InterruptedException {

        var id = UUID.randomUUID();
        var prediction = Prediction.builder()
                .id(id)
                .build();

        tensorflowPredictionService.predict(TensorflowPrediction.builder()
                .prediction(prediction)
                .predictions(Predictions.PREDICT_RESULTS)
                .country("any")
                .build());

        Thread.sleep(100);

        verify(webClientFacade, atLeastOnce()).predict("trainingresults/any/" + id.toString(), prediction);

    }
}