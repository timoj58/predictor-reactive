package com.timmytime.predictorclientreactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TopSelection {
    private String label;
    private String subtitle;
    private String market;
    private Double rating;
    private String date;
}
