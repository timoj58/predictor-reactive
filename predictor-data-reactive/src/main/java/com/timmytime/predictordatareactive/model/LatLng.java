package com.timmytime.predictordatareactive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class LatLng implements Serializable {

    private Double latitude;
    private Double longitude;

}
