package com.timmytime.predictorplayerseventsreactive.request;

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
