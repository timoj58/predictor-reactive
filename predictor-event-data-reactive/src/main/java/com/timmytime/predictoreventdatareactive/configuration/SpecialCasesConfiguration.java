package com.timmytime.predictoreventdatareactive.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecialCasesConfiguration {

    @JsonProperty
    private List<SpecialCase> specialCases;

    public SpecialCasesConfiguration() {

    }

    public List<SpecialCase> getSpecialCases() {
        return specialCases;
    }

    public void setSpecialCases(List<SpecialCase> specialCases) {
        this.specialCases = specialCases;
    }

}
