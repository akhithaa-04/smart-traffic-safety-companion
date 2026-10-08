package com.stsc.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Smart Traffic Safety Companion backend.
 *
 * Run with: mvn spring-boot:run
 * H2 console (dev profile): http://localhost:8080/h2-console
 */
@SpringBootApplication
public class SmartTrafficSafetyCompanionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTrafficSafetyCompanionApplication.class, args);
    }
}
