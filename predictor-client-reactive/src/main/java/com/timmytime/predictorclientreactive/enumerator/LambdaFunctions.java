package com.timmytime.predictorclientreactive.enumerator;

public enum LambdaFunctions {
    /*
     cloud watch: starts config server and then this service.
     */
    DATABASE("predictor-init"), //starts DB
    PROXY_START("proxy-start"),
    PRE_START("pre-start"), //starts data + scrapers + machine learning
    START("start"), //starts everything else. (data events, teams, players, events)
    PROXY_STOP("proxy-stop"),
    SHUTDOWN_ML_TEAMS("ml-team-stop"),
    SHUTDOWN_ML_PLAYERS("ml-players-stop"),
    SHUTDOWN("predictor-destroy") //shuts down all the instances
    ;

    private final String functionName;

    LambdaFunctions(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}
