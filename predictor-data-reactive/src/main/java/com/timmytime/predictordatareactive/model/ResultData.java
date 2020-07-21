package com.timmytime.predictordatareactive.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.UUID;


@Getter
@Setter
public class ResultData {

    private Integer id;
    private JSONObject match;
    private JSONObject lineups;
    private JSONObject result;

    public ResultData(Result result){

        this.id = result.getMatchId();
        this.result = new JSONObject(result.getResult());
        this.lineups = new JSONObject(result.getLineup());
        this.match = new JSONObject(result.getMatch());

    }
}
