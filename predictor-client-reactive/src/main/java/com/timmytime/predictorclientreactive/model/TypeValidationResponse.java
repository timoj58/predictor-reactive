package com.timmytime.predictorclientreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TypeValidationResponse implements Serializable {

    private String type;
    private Map<String, String> validations = new HashMap<>();

    public TypeValidationResponse(String type, String key, String value) {
        this.type = type;
        if(value != null && !value.trim().isEmpty()) {
            this.validations.put(key, value);
        }
    }

}
