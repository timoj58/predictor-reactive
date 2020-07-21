package com.timmytime.predictoreventscraperreactive.facade;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.timmytime.predictoreventscraperreactive.request.ScraperRequest;
import com.timmytime.predictoreventscraperreactive.response.ScraperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


@Component
public class ScraperProxyFacade {

    private final Logger log = LoggerFactory.getLogger(ScraperProxyFacade.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private String scraperProxyHost;// = "http://ec2-3-250-88-102.eu-west-1.compute.amazonaws.com:8080";
    private final String profile;

    @Autowired
    public ScraperProxyFacade(
            @Value("${aws.profile}") String profile
    ){
        this.profile = profile;

        if(profile == null){
            profile = "";
        }
    }

    public ScraperResponse scrape(String type, String method, ScraperRequest scraperRequest) {

        HttpEntity<?> entity = new HttpEntity<>(scraperRequest, null);

        return restTemplate.postForEntity(
                scraperProxyHost + "/api/regional/proxy/scrape/{method}/{type}"
                        .replace("{type}", type)
                        .replace("{method}", method),
                entity,
                ScraperResponse.class).getBody();

    }

    @PostConstruct
    public void start(){
        //i-070a558ab55e283a9

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("arn:aws:lambda:us-east-1:842788105885:function:proxy-describe");

        InvokeResult invokeResult = null;

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider(profile))
                    .withRegion(Regions.US_EAST_1).build();

            invokeResult = awsLambda.invoke(invokeRequest);

            String url = new String(invokeResult.getPayload().array()).replace("\"", "");;

            scraperProxyHost = "http://"+url+":8080";

            log.info("scraper is located at {}", scraperProxyHost);

        } catch (ServiceException e) {
            log.error("lambda", e);
        }


    }


}
