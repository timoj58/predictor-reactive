package com.timmytime.predictorclientreactive.enumerator;

public enum LambdaFunctions {
    /*
     cloud watch: starts config server and then this service.
     */
    DATABASE("arn:aws:lambda:us-east-1:842788105885:function:predictor-init"), //starts DB
    PRE_START("arn:aws:lambda:us-east-1:842788105885:function:pre-start"), //starts data + scrapers + machine learning
    START("arn:aws:lambda:us-east-1:842788105885:function:start"), //starts everything else. (data events, teams, players, events)
    PROXY_STOP("arn:aws:lambda:us-east-1:842788105885:function:proxy-stop"),
    SHUTDOWN_ML_TEAMS(""),
    SHUTDOWN_ML_PLAYERS(""),
    SHUTDOWN("arn:aws:lambda:us-east-1:842788105885:function:predictor-destroy") //shuts down all the instances
    ;

    public String getFunctionName() {
        return functionName;
    }

    private final String functionName;

    LambdaFunctions(String functionName) {
        this.functionName = functionName;
    }
}
