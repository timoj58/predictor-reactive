package com.timmytime.predictordatareactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DataLoadError {

    @Id
    private UUID id;
    private String data;
    private String homeTeam;
    private String awayTeam;
    private String player;

}


