package com.timmytime.predictorclientreactive.request;

import com.timmytime.predictorclientreactive.enumerator.Messages;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    private Messages type;
}
