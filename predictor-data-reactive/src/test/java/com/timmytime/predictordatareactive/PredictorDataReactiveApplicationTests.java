package com.timmytime.predictordatareactive;

import com.timmytime.predictordatareactive.configuration.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PredictorDataReactiveApplicationTests {

    @Autowired
    private DataConfig missingTeams;

    @Test
    void contextLoads() {
    }

    @Test
    void config(){
        System.out.println(missingTeams.getCountries().size());
    }

}
