package com.timmytime.predictorclientreactive.facade;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.springframework.stereotype.Component;

@Component
public class LambdaFacade {

    public void invoke(String functionName) {
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("arn:aws:lambda:us-east-1:842788105885:function:"+functionName);

        InvokeResult invokeResult = null;

        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion(Regions.US_EAST_1).build();

        invokeResult = awsLambda.invoke(invokeRequest);
    }

}
