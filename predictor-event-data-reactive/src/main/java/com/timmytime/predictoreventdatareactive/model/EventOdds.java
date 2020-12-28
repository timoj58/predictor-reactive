package com.timmytime.predictoreventdatareactive.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOdds {

    @Id
    private UUID id;
    private String event;
    private String competition;
    private Double price;
    private List<UUID> teams = new ArrayList<>();
    private LocalDateTime eventDate;
    private LocalDateTime timestamp;
    private String provider;

}
