package com.stsc.backend.controller;

import com.stsc.backend.dto.ConfirmDismissRequest;
import com.stsc.backend.dto.IncidentReportRequest;
import com.stsc.backend.model.IncidentReport;
import com.stsc.backend.service.IncidentReportService;
import com.stsc.backend.service.SafetyColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IncidentReportController {

    private final IncidentReportService incidentReportService;

    /** Voluntary citizen reporting - works whether or not the citizen is on an active journey. */
    @PostMapping
    public IncidentReport submit(@RequestBody IncidentReportRequest request) {
        return incidentReportService.submitReport(request);
    }

    @GetMapping
    public List<IncidentReport> allActive() {
        return incidentReportService.findAllActive();
    }

    /** Reports relevant to a user's current location/journey - the "incident ahead" check. */
    @GetMapping("/nearby")
    public List<IncidentReport> nearby(@RequestParam double lat,
                                        @RequestParam double lng,
                                        @RequestParam(defaultValue = "1000") double radiusMeters) {
        return incidentReportService.findNearbyActive(lat, lng, radiusMeters);
    }

    /** Green / Yellow / Red indicator for a location. */
    @GetMapping("/safety-color")
    public Map<String, SafetyColorService.SafetyColor> safetyColor(@RequestParam double lat,
                                                                      @RequestParam double lng,
                                                                      @RequestParam(defaultValue = "1000") double radiusMeters) {
        return Map.of("color", incidentReportService.safetyColorFor(lat, lng, radiusMeters));
    }

    @PostMapping("/{id}/respond")
    public IncidentReport respond(@PathVariable Long id, @RequestBody ConfirmDismissRequest request) {
        return incidentReportService.confirmOrDismiss(id, request);
    }
}
