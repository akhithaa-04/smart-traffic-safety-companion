package com.stsc.backend.dto;

import lombok.Data;

@Data
public class TravelMemoryRequest {
    private Long userId;
    private double latitude;
    private double longitude;
    private String placeName;
    private String note;
    private String photoUrl;
}
