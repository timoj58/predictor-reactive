package com.timmytime.predictormessagereactive.model;

import com.timmytime.predictormessagereactive.request.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CycleEvent {

    private final Message message;
    private final LocalDateTime timestamp;

    public CycleEvent(Message message){
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}
