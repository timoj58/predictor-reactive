package com.timmytime.predictorplayersreactive.service.impl;

import com.timmytime.predictorplayersreactive.service.FantasyOutcomeService;
import com.timmytime.predictorplayersreactive.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("validationService")
public class ValidationServiceImpl implements ValidationService {

    private final FantasyOutcomeService fantasyOutcomeService;

    @Autowired
    public ValidationServiceImpl(
            FantasyOutcomeService fantasyOutcomeService
    ){
        this.fantasyOutcomeService = fantasyOutcomeService;
    }

    @Override
    public void validate() {

    }
}
