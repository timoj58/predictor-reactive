package com.timmytime.predictormessagereactive.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileRequest {
    @Id
    private String key;
    private String content;
}
