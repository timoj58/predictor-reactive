package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.LambdaFacade;
import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.StartupService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class StartupServiceImplTest {

    private final LambdaFacade lambdaFacade = mock(LambdaFacade.class);
    private final WebClientFacade webClientFacade = mock(WebClientFacade.class);
    private final S3Facade s3Facade = mock(S3Facade.class);

    private final StartupService startupService
            = new StartupServiceImpl(
            0,
            "",
            "",
            lambdaFacade,
            webClientFacade,
            s3Facade
    );

    @Test
    public void test() throws InterruptedException {

        startupService.start();

        Thread.sleep(1000L);

        verify(s3Facade, atLeast(6)).archive(anyString());
        verify(lambdaFacade, atLeast(3)).invoke(anyString());
        verify(webClientFacade, atLeast(2)).startScraper(anyString());

    }


}