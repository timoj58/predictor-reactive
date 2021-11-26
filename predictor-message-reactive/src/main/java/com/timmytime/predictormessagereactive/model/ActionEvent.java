package com.timmytime.predictormessagereactive.model;

import com.timmytime.predictormessagereactive.enumerator.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ActionEvent {
    private final Action action;
    private final LocalDateTime timestamp;
}
