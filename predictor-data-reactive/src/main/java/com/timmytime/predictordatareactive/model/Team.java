package com.timmytime.predictordatareactive.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Team {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private String label;
    @Indexed
    @EqualsAndHashCode.Include
    private String country;
    @EqualsAndHashCode.Include
    private String competition;
    private LatLng latLng;
    private String espnId;

}
