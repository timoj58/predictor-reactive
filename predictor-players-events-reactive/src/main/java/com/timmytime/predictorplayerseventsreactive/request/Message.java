package com.timmytime.predictorplayerseventsreactive.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Message {
    private String event;
    private String eventType;
}
