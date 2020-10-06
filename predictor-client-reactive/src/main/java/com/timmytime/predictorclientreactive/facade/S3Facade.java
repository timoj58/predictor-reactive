package com.timmytime.predictorclientreactive.facade;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class S3Facade implements IS3Facade {

    @Override
    public void put(String key, String json) {

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion(Regions.US_EAST_1).build();

        s3.putObject("predictor-client-data", key, json);

    }

    @Override
    public void archive() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion(Regions.US_EAST_1).build();


        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName("predictor-client-data")
                .withMaxKeys(250);
        ListObjectsV2Result result;

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        do {

            result = s3.listObjectsV2(listObjectsRequest);
            result.getObjectSummaries()
                    .stream()
                    .filter(f -> !f.getKey().contains("archive"))
                    .forEach(summary ->
                            Mono.just(summary)
                                    .doOnNext(details ->
                                            s3.copyObject(new CopyObjectRequest(
                                                    details.getBucketName(),
                                                    details.getKey(),
                                                    details.getBucketName(),
                                                    "archive/" + date + "/" + details.getBucketName()))
                                    ).doFinally(delete -> s3.deleteObject(new DeleteObjectRequest(summary.getBucketName(), summary.getKey())))
                    );

        } while (result.isTruncated());

    }
}
