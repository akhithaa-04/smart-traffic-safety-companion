package com.stsc.backend.dto;

import lombok.Data;

@Data
public class NearbyReportsQuery {
    private double latitude;
    private double longitude;
    private double radiusMeters = 1000; // default proximity radius
}
