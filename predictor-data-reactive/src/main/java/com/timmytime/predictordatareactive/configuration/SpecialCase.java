package com.timmytime.predictordatareactive.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialCase {

    private String name;
    private String alias;

    public SpecialCase() {

    }

    public SpecialCase(String name) {
        this.name = name;
    }

}
