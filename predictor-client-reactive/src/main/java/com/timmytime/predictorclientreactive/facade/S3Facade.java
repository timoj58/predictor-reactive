package com.timmytime.predictorclientreactive.facade;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.timmytime.predictorclientreactive.request.FileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static java.time.LocalDate.now;
import static reactor.core.publisher.Mono.just;

@Slf4j
@Component
public class S3Facade implements IS3Facade {

    @Value("${test.mode}")
    private final Boolean testMode;
    @Value("${clients.message}")
    private final String messageHost;
    private final AmazonS3 amazonS3;
    private final WebClientFacade webClientFacade;

    @Autowired
    public S3Facade(
            @Value("${test.mode}") Boolean testMode,
            @Value("${clients.message}") String messageHost,
            AmazonS3 amazonS3,
            WebClientFacade webClientFacade
    ){
        this.testMode = testMode;
        this.messageHost = messageHost;
        this.webClientFacade = webClientFacade;
        this.amazonS3 = amazonS3;

    }

    @Override
    public void put(String key, String json) {
        if (testMode)
            webClientFacade.put(messageHost+"/file", FileRequest.builder()
                    .content(json)
                            .key(key)
                    .build());
        else
            amazonS3.putObject("predictor-client-data", key, json);
    }

    @Override
    public void put(String bucket, String key, String csv) {
        if(testMode)
            webClientFacade.put(messageHost+"/file", FileRequest.builder()
                    .content(csv)
                            .key(key)
                    .build());
        else
         amazonS3.putObject(bucket, key, csv);
    }

    @Override
    public void delete(String folder) {
        if(!testMode) {

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

    }


    @Override
    public void archive(String prefix) {
        if(!testMode) {
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
}
