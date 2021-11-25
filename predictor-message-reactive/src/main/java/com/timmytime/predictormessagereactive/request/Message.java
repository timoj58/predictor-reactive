package com.timmytime.predictormessagereactive.request;

import com.timmytime.predictormessagereactive.enumerator.Event;
import com.timmytime.predictormessagereactive.enumerator.EventType;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class Message {

    private final Event event;
    private final EventType eventType;

}
