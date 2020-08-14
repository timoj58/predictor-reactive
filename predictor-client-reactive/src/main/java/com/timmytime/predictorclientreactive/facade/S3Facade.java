package com.timmytime.predictorclientreactive.facade;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
public class S3Facade implements IS3Facade{

    @Override
    public void put(String key, String json){

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion(Regions.US_EAST_1).build();

        s3.putObject("predictor-client-data", key, json);

    }
}
