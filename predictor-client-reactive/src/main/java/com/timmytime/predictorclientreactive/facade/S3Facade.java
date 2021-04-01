package com.timmytime.predictorclientreactive.facade;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import static java.time.LocalDate.now;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Component
public class S3Facade implements IS3Facade {

    Supplier<AmazonS3> amazonS3Supplier = () -> AmazonS3ClientBuilder.standard()
            .withCredentials(new InstanceProfileCredentialsProvider(false))
            .withRegion(Regions.US_EAST_1).build();

    @Override
    public void put(String key, String json) {
        amazonS3Supplier.get().putObject("predictor-client-data", key, json);
    }

    @Override
    public void put(String bucket, String key, String csv) {
        amazonS3Supplier.get().putObject(bucket, key, csv);
    }

    @Override
    public void delete(String folder) {
        final AmazonS3 s3 = amazonS3Supplier.get();

        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName("predictor-client-data")
                .withPrefix(folder)
                .withMaxKeys(250);


        ListObjectsV2Result result;
        do {

            log.info("deleting files");

            result = s3.listObjectsV2(listObjectsRequest);
            result.getObjectSummaries()
                    .forEach(key -> {
                        log.info("deleting {}", key.getKey());
                        s3.deleteObject(key.getBucketName(), key.getKey());
                    });
        } while (result.isTruncated());

    }


    @Override
    public void archive(String prefix) {
        log.info("archiving {}", prefix);
        final AmazonS3 s3 = amazonS3Supplier.get();


        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName("predictor-client-data")
                .withPrefix(prefix + "/")
                .withMaxKeys(250);
        ListObjectsV2Result result;

        String date = now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        do {

            result = s3.listObjectsV2(listObjectsRequest);
            result.getObjectSummaries()
                    .forEach(summary ->
                            just(summary)
                                    .doOnNext(details ->
                                            s3.copyObject(new CopyObjectRequest(
                                                    details.getBucketName(),
                                                    details.getKey(),
                                                    details.getBucketName(),
                                                    "archive/" + date + "/" + details.getKey()))
                                    ).doFinally(delete -> s3.deleteObject(new DeleteObjectRequest(summary.getBucketName(), summary.getKey())))
                                    .subscribe()
                    );

        } while (result.isTruncated());

    }
}
