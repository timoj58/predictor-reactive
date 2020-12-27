package com.timmytime.predictoreventscraperreactive.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScraperRequest {

    private String url;
    private HttpEntity<?> httpEntity;


}
