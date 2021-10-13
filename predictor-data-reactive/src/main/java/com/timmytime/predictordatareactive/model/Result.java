package com.timmytime.predictordatareactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@Document
@Getter
@Setter
public class Result {

    @Id
    private Integer matchId;
    private LocalDate date;
    private String lineup;
    private String result;
    private Boolean processed = Boolean.FALSE;

    public Result(Integer id) {
        this.matchId = id;
        this.date = LocalDate.now();
    }

    public Boolean ready() {
        return lineup != null && result != null;
    }
}
