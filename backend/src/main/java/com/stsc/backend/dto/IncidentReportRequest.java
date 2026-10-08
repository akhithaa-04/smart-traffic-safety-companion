package com.stsc.backend.dto;

import com.stsc.backend.model.IncidentReport;
import lombok.Data;

@Data
public class IncidentReportRequest {
    private Long reporterId;
    private IncidentReport.IncidentType type;
    private double latitude;
    private double longitude;
    private String description;
    private String cause;
    private String photoUrl;
    private Integer estimatedDurationMinutes;
    private String suggestedAlternateRoute;
}
