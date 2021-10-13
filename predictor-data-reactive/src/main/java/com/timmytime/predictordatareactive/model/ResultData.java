package com.timmytime.predictordatareactive.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;


@Getter
@Setter
public class ResultData {

    private Integer id;
    private JSONObject lineups;
    private JSONObject result;

    public ResultData(Result result) {

        this.id = result.getMatchId();
        this.result = new JSONObject(result.getResult());
        this.lineups = new JSONObject(result.getLineup());

    }
}
