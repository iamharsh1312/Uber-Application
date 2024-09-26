package com.project.uber.uberApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PointDto {

    private double[] coordinates;
    private String type ="Point";

    public PointDto(double[] coordinates) {
        this.coordinates = coordinates;
    }

}
