package com.stsc.backend.controller;

import com.stsc.backend.dto.DrivingDetectionRequest;
import com.stsc.backend.dto.JourneyEndRequest;
import com.stsc.backend.dto.JourneyStartRequest;
import com.stsc.backend.model.Journey;
import com.stsc.backend.service.DrivingDetectionService;
import com.stsc.backend.service.JourneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JourneyController {

    private final JourneyService journeyService;
    private final DrivingDetectionService drivingDetectionService;

    /** Client calls this periodically with recent GPS samples summarised. */
    @PostMapping("/detect")
    public Map<String, Boolean> detect(@RequestBody DrivingDetectionRequest request) {
        return Map.of("isDrivingJourney", drivingDetectionService.isDrivingJourney(request));
    }

    /** Call once detect() returns true - creates the journey & returns the voice reminder. */
    @PostMapping("/start")
    public JourneyService.JourneyStartResult start(@RequestBody JourneyStartRequest request) {
        return journeyService.startJourney(request);
    }

    @PostMapping("/{id}/confirm-safety-check")
    public Map<String, String> confirmSafetyCheck(@PathVariable Long id) {
        journeyService.confirmSafetyCheck(id);
        return Map.of("status", "confirmed");
    }

    @PostMapping("/{id}/end")
    public Journey end(@PathVariable Long id, @RequestBody JourneyEndRequest request) {
        return journeyService.endJourney(id, request);
    }

    @GetMapping("/user/{userId}")
    public List<Journey> history(@PathVariable Long userId) {
        return journeyService.getHistory(userId);
    }
}
