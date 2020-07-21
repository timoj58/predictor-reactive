package com.timmytime.predictorplayersreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document
public class PlayersByYear {

    @Id
    private Integer year;
    private Set<UUID> players = new HashSet<>();

}
