package com.timmytime.predictormessagereactive.handler;

import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestHandler {

    private final WebClientFacade webClientFacade;
}
