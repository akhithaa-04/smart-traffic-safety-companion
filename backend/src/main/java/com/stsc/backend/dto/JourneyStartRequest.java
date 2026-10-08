package com.stsc.backend.dto;

import com.stsc.backend.model.User;
import lombok.Data;

@Data
public class JourneyStartRequest {
    private Long userId;
    private double latitude;
    private double longitude;
    // Which vehicle this specific trip is using (relevant if user profile = BOTH)
    private User.VehicleType vehicleTypeUsed;
    // Optional context signals used to vary the safety reminder
    private boolean raining;
    private boolean nightTime;
}
