package com.stsc.backend.config;

import com.stsc.backend.model.*;
import com.stsc.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-demo-data:true}")
    private boolean seedEnabled;

    @Override
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) return;

        String demoPassword = passwordEncoder.encode("password123");

        User asha = userRepository.save(User.builder()
                .name("Asha Rao")
                .email("asha@example.com")
                .password(demoPassword)
                .vehicleType(User.VehicleType.TWO_WHEELER)
                .points(20)
                .safeStartStreak(3)
                .build());

        User vikram = userRepository.save(User.builder()
                .name("Vikram Shah")
                .email("vikram@example.com")
                .password(demoPassword)
                .vehicleType(User.VehicleType.CAR)
                .points(35)
                .safeStartStreak(5)
                .build());

        userRepository.save(User.builder()
                .name("Demo Citizen")
                .email("citizen@example.com")
                .password(demoPassword)
                .vehicleType(User.VehicleType.BOTH)
                .build());

        badgeRepository.save(Badge.builder()
                .name("First Report")
                .description("Submitted your first citizen report")
                .criteriaType(Badge.CriteriaType.POINTS)
                .threshold(10)
                .build());

        badgeRepository.save(Badge.builder()
                .name("Trusted Reporter")
                .description("Earned 50+ points from accurate contributions")
                .criteriaType(Badge.CriteriaType.POINTS)
                .threshold(50)
                .build());

        badgeRepository.save(Badge.builder()
                .name("Safety First")
                .description("5 consecutive confirmed pre-journey safety checks")
                .criteriaType(Badge.CriteriaType.SAFE_START_STREAK)
                .threshold(5)
                .build());

        incidentReportRepository.save(IncidentReport.builder()
                .reporter(asha)
                .type(IncidentReport.IncidentType.ROAD_CONSTRUCTION)
                .latitude(17.4475)
                .longitude(78.3563)
                .description("Lane closed for drainage work near Jubilee Hills check post")
                .cause("Municipal drainage work")
                .estimatedDurationMinutes(180)
                .suggestedAlternateRoute("Use the service road via Road No. 36")
                .confirmCount(2)
                .build());

        incidentReportRepository.save(IncidentReport.builder()
                .reporter(vikram)
                .type(IncidentReport.IncidentType.FESTIVAL_OR_PROCESSION)
                .latitude(17.3850)
                .longitude(78.4867)
                .description("Ganesh Chaturthi procession moving through Abids main road")
                .cause("Festival procession")
                .estimatedDurationMinutes(240)
                .suggestedAlternateRoute("Take the Koti flyover instead of Abids main road")
                .confirmCount(3)
                .build());

        System.out.println("Demo data seeded: 3 users, 3 badges, 2 incident reports.");
    }
}