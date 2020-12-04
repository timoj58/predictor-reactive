package com.timmytime.predictoreventsreactive.service;


import java.util.function.Consumer;

public interface ValidationService {
    void validate(String county);

    void resetLast(String country, Consumer<String> doFinally);
}
