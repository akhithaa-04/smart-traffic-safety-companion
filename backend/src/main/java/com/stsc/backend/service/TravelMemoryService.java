package com.stsc.backend.service;

import com.stsc.backend.config.AppConstants;
import com.stsc.backend.dto.TravelMemoryRequest;
import com.stsc.backend.model.TravelMemory;
import com.stsc.backend.model.User;
import com.stsc.backend.repository.TravelMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelMemoryService {

    private final TravelMemoryRepository memoryRepository;
    private final UserService userService;

    /**
     * Called by the client once it detects the user has stayed near one spot
     * for >= MEANINGFUL_VISIT_MINUTES. Saves it as a new memory.
     */
    public TravelMemory saveMemory(TravelMemoryRequest request) {
        User user = userService.getById(request.getUserId());

        TravelMemory memory = TravelMemory.builder()
                .user(user)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .placeName(request.getPlaceName())
                .note(request.getNote())
                .photoUrl(request.getPhotoUrl())
                .build();

        return memoryRepository.save(memory);
    }

    /**
     * Checks whether the user's current location matches a past memory
     * (within the geofence radius). If so, returns a gentle "Journey Echo"
     * message and marks it resurfaced. Returns empty if nothing nearby.
     */
    public Optional<String> checkForEcho(Long userId, double lat, double lng) {
        User user = userService.getById(userId);
        List<TravelMemory> memories = memoryRepository.findByUserOrderByVisitedAtDesc(user);

        for (TravelMemory memory : memories) {
            double distance = GeoUtil.distanceMeters(lat, lng, memory.getLatitude(), memory.getLongitude());
            if (distance <= AppConstants.MEMORY_GEOFENCE_RADIUS_METERS) {
                long daysAgo = ChronoUnit.DAYS.between(memory.getVisitedAt(), LocalDateTime.now());
                memory.setLastResurfacedAt(LocalDateTime.now());
                memory.setTimesRevisited(memory.getTimesRevisited() + 1);
                memoryRepository.save(memory);

                String place = memory.getPlaceName() != null ? memory.getPlaceName() : "this place";
                String timeAgo = daysAgo <= 0 ? "earlier today"
                        : daysAgo == 1 ? "yesterday"
                        : daysAgo + " days ago";

                String base = "You were at " + place + " " + timeAgo + ".";
                return Optional.of(memory.getNote() != null && !memory.getNote().isBlank()
                        ? base + " " + memory.getNote()
                        : base);
            }
        }
        return Optional.empty();
    }

    public List<TravelMemory> getHistory(Long userId) {
        return memoryRepository.findByUserOrderByVisitedAtDesc(userService.getById(userId));
    }
}
