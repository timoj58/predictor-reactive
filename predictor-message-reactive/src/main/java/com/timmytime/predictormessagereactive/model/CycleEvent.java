package com.timmytime.predictormessagereactive.model;

import com.timmytime.predictormessagereactive.request.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class CycleEvent {

    private final Message message;
    private final LocalDateTime timestamp;

    public CycleEvent(Message message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}
