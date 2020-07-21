package com.timmytime.predictorplayersreactive.request;

import com.timmytime.predictorplayersreactive.enumerator.Messages;
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
