package com.timmytime.predictorteamsreactive.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String event;
    private String eventType;
}
