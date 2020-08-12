package com.timmytime.predictorclientreactive.enumerator;

public enum LambdaFunctions {
    /*
     cloud watch: starts config server and then this service.
     */
    INIT(""), //starts DB + proxy
    PRE_START(""), //starts data + scrapers + machine learning
    START(""), //starts everything else. (data events, teams, players, events)
    SHUTDOWN("") //shuts down all the instances
    ;

    public String getFunctionName() {
        return functionName;
    }

    private String functionName;

    LambdaFunctions(String functionName){
        this.functionName = functionName;
    }
}
