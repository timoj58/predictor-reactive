package com.timmytime.predictorclientreactive.request;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class FileRequest{
    private String key;
    private String content;
}
