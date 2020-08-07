package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.S3Facade;
import com.timmytime.predictorclientreactive.facade.WebClientFacade;
import com.timmytime.predictorclientreactive.service.ILoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("competitionService")
public class CompetitionServiceImpl implements ILoadService {

    private final S3Facade s3Facade;
    private final WebClientFacade webClientFacade;

    @Autowired
    public CompetitionServiceImpl(
            S3Facade s3Facade,
            WebClientFacade webClientFacade
    ){
        this.s3Facade = s3Facade;
        this.webClientFacade = webClientFacade;
    }

    @Override
    public void load() {
        /*
          saves Competition related things.  easy enough.
         */

    }
}
