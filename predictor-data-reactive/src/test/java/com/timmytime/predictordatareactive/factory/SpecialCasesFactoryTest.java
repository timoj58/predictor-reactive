package com.timmytime.predictordatareactive.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SpecialCasesFactoryTest {

    private final SpecialCasesFactory specialCasesFactory
            = new SpecialCasesFactory("./src/main/resources/config/");


    @Test
    public void loadConfigTest() {
        assertFalse(specialCasesFactory.getSpecialCasesConfiguration().getSpecialCases().isEmpty());
    }
}