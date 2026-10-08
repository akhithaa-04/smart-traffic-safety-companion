package com.stsc.backend.service;

import com.stsc.backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Generates the pre-journey voice/notification safety reminder.
 *
 * Design goals (from project discussion):
 *  - Rotate between several phrases per vehicle type, so it never feels
 *    like a single repeated robotic line.
 *  - Sound like a companion checking in, not a system alert.
 *  - Lightly adapt to context (rain, night) when that info is available.
 *  - Keep it short - the user should not need to read/listen to anything
 *    long while they are about to start driving.
 */
@Service
public class SafetyReminderService {

    private final Random random = new Random();

    private static final Map<User.VehicleType, List<String>> BASE_PHRASES = Map.of(
            User.VehicleType.TWO_WHEELER, List.of(
                    "Helmet on? Let's roll safely.",
                    "Quick check - helmet secured?",
                    "Ready when your helmet is.",
                    "Helmet first, then let's go.",
                    "Gear check: helmet on, you're good to ride."
            ),
            User.VehicleType.CAR, List.of(
                    "Buckled up? Good, let's go.",
                    "Seatbelt check before we start.",
                    "Quick check - seatbelt on?",
                    "Belt secured? Alright, let's drive.",
                    "One click for safety - seatbelt on?"
            )
    );

    private static final List<String> RAIN_SUFFIXES = List.of(
            " It's wet out there, take it easy today.",
            " Roads are slippery right now, extra care please."
    );

    private static final List<String> NIGHT_SUFFIXES = List.of(
            " Late one tonight, stay alert out there.",
            " Driving at night, watch your speed."
    );

    public String generateReminder(User.VehicleType vehicleType, boolean raining, boolean nightTime) {
        // BOTH shouldn't reach here in practice (caller resolves the trip's
        // actual vehicle first) - fall back to CAR phrasing defensively.
        User.VehicleType effectiveType = (vehicleType == null || vehicleType == User.VehicleType.BOTH)
                ? User.VehicleType.CAR
                : vehicleType;

        List<String> pool = BASE_PHRASES.get(effectiveType);
        String base = pool.get(random.nextInt(pool.size()));

        StringBuilder message = new StringBuilder(base);

        if (raining) {
            message.append(RAIN_SUFFIXES.get(random.nextInt(RAIN_SUFFIXES.size())));
        } else if (nightTime) {
            message.append(NIGHT_SUFFIXES.get(random.nextInt(NIGHT_SUFFIXES.size())));
        }

        return message.toString();
    }
}
