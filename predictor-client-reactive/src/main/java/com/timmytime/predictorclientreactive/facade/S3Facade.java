package com.timmytime.predictorclientreactive.facade;

import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
public class S3Facade implements IS3Facade{

    @Override
    public void put(String key, String json){

        //tag current to start...we move the rest to historic first

        //TODO.
    }
}
