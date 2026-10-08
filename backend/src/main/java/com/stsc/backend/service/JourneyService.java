package com.stsc.backend.service;

import com.stsc.backend.dto.JourneyEndRequest;
import com.stsc.backend.dto.JourneyStartRequest;
import com.stsc.backend.model.Journey;
import com.stsc.backend.model.User;
import com.stsc.backend.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final UserService userService;
    private final SafetyReminderService safetyReminderService;
    private final RewardService rewardService;

    /**
     * Called once DrivingDetectionService confirms a genuine driving journey
     * has begun. Creates the Journey record and generates the personalised,
     * rotating safety reminder for the client to speak/display.
     */
    public JourneyStartResult startJourney(JourneyStartRequest request) {
        User user = userService.getById(request.getUserId());

        User.VehicleType tripVehicleType = request.getVehicleTypeUsed() != null
                ? request.getVehicleTypeUsed()
                : (user.getVehicleType() == User.VehicleType.BOTH ? User.VehicleType.CAR : user.getVehicleType());

        Journey journey = Journey.builder()
                .user(user)
                .startLat(request.getLatitude())
                .startLng(request.getLongitude())
                .vehicleTypeUsed(tripVehicleType)
                .build();

        journey = journeyRepository.save(journey);

        String reminder = safetyReminderService.generateReminder(
                tripVehicleType, request.isRaining(), request.isNightTime());

        return new JourneyStartResult(journey, reminder);
    }

    /** Called when the user taps/says "Got it" on the pre-journey safety reminder. */
    public void confirmSafetyCheck(Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Journey not found: " + journeyId));
        journey.setSafetyCheckConfirmed(true);
        journeyRepository.save(journey);

        rewardService.registerSafeStart(journey.getUser());
    }

    public Journey endJourney(Long journeyId, JourneyEndRequest request) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Journey not found: " + journeyId));

        journey.setEndLat(request.getLatitude());
        journey.setEndLng(request.getLongitude());
        journey.setEndedAt(LocalDateTime.now());
        journey.setStatus(Journey.JourneyStatus.COMPLETED);

        if (!journey.isSafetyCheckConfirmed()) {
            rewardService.resetSafeStartStreak(journey.getUser());
        }

        Journey savedJourney = journeyRepository.save(journey);

        // Force the User entity to be initialized before Jackson serializes the response
        savedJourney.getUser().getId();

        return savedJourney;
    }

    public java.util.List<Journey> getHistory(Long userId) {
        return journeyRepository.findByUserOrderByStartedAtDesc(userService.getById(userId));
    }

    /** Simple holder so the controller can return both the journey and the voice line together. */
    public record JourneyStartResult(Journey journey, String safetyReminder) {}
}
