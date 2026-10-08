package com.stsc.backend.controller;

import com.stsc.backend.dto.TravelMemoryRequest;
import com.stsc.backend.model.TravelMemory;
import com.stsc.backend.service.TravelMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/memories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TravelMemoryController {

    private final TravelMemoryService travelMemoryService;

    @PostMapping
    public TravelMemory save(@RequestBody TravelMemoryRequest request) {
        return travelMemoryService.saveMemory(request);
    }

    @GetMapping("/user/{userId}")
    public List<TravelMemory> history(@PathVariable Long userId) {
        return travelMemoryService.getHistory(userId);
    }

    /** Call when the app detects the user has entered a geofence - checks for a "Journey Echo". */
    @GetMapping("/echo")
    public Map<String, String> checkEcho(@RequestParam Long userId,
                                          @RequestParam double lat,
                                          @RequestParam double lng) {
        Optional<String> echo = travelMemoryService.checkForEcho(userId, lat, lng);
        return echo.map(msg -> Map.of("echo", msg)).orElse(Map.of());
    }
}
