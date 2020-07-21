package com.timmytime.predictorscraperreactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Getter
@Setter
@NoArgsConstructor
public class ScraperHistory {

    @Id
    private UUID id;

    private LocalDateTime date;
    private Integer daysScraped;

}
