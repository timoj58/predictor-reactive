package com.timmytime.predictoreventscraperreactive.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;

@Getter
@Setter
public class ScraperRequest {

    private String url;
    private HttpEntity<?> httpEntity;

    public ScraperRequest() {

    }

    public ScraperRequest(String url, HttpEntity<?> httpEntity) {
        this.url = url;
        this.httpEntity = httpEntity;

    }

}
