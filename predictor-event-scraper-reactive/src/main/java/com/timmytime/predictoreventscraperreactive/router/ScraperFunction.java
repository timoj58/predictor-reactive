package com.timmytime.predictoreventscraperreactive.router;

import com.timmytime.predictoreventscraperreactive.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
@RequiredArgsConstructor
public class ScraperFunction {

    private final ScraperService scraperService;

    @Bean
    @RouterOperation(beanClass = ScraperService.class, beanMethod = "scrape")
    RouterFunction<ServerResponse> scrape() {

        return route(RequestPredicates.POST("/scrape"),
                (scrape) -> ServerResponse.ok().build(scraperService.scrape()));
    }
}
