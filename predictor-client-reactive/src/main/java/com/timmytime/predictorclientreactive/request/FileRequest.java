package com.timmytime.predictorclientreactive.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRequest {
    private String key;
    private String content;
}
