package com.timmytime.predictorplayerseventsreactive.request;

import com.timmytime.predictorplayerseventsreactive.enumerator.Messages;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private Messages type;
    private String country;
}
