package com.timmytime.predictoreventsreactive.request;

import com.timmytime.predictoreventsreactive.enumerator.Messages;
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
