package com.timmytime.predictorclientreactive.facade;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static java.time.LocalDate.now;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Component
@AllArgsConstructor
public class S3Facade implements IS3Facade {

    private final AmazonS3 amazonS3;

    @Override
    public void put(String key, String json) {
        amazonS3.putObject("predictor-client-data", key, json);
    }

    @Override
    public void put(String bucket, String key, String csv) {
        amazonS3.putObject(bucket, key, csv);
    }

    @Override
    public void delete(String folder) {

        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName("predictor-client-data")
                .withPrefix(folder)
                .withMaxKeys(250);


        ListObjectsV2Result result;
        do {

            log.info("deleting files");

            result = amazonS3.listObjectsV2(listObjectsRequest);
            result.getObjectSummaries()
                    .forEach(key -> {
                        log.info("deleting {}", key.getKey());
                        amazonS3.deleteObject(key.getBucketName(), key.getKey());
                    });
        } while (result.isTruncated());

    }


    @Override
    public void archive(String prefix) {
        log.info("archiving {}", prefix);


        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName("predictor-client-data")
                .withPrefix(prefix + "/")
                .withMaxKeys(250);
        ListObjectsV2Result result;

        String date = now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        do {

            result = amazonS3.listObjectsV2(listObjectsRequest);
            result.getObjectSummaries()
                    .forEach(summary ->
                            just(summary)
                                    .doOnNext(details ->
                                            amazonS3.copyObject(new CopyObjectRequest(
                                                    details.getBucketName(),
                                                    details.getKey(),
                                                    details.getBucketName(),
                                                    "archive/" + date + "/" + details.getKey()))
                                    ).doFinally(delete -> amazonS3.deleteObject(new DeleteObjectRequest(summary.getBucketName(), summary.getKey())))
                                    .subscribe()
                    );

        } while (result.isTruncated());

    }
}
