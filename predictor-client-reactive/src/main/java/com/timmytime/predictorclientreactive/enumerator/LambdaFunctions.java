package com.timmytime.predictorclientreactive.enumerator;

public enum LambdaFunctions {
    START("start"),
    SHUTDOWN_ML_TEAMS("ml-team-stop"),
    SHUTDOWN_ML_PLAYERS("ml-players-stop"),
    SHUTDOWN("predictor-destroy");

    private final String functionName;

    LambdaFunctions(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}
