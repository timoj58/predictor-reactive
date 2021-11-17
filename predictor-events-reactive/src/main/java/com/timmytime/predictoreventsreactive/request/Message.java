package com.timmytime.predictoreventsreactive.request;

import com.timmytime.predictoreventsreactive.enumerator.Messages;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private Messages type;
    private String country;
}
