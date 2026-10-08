package com.stsc.backend.repository;

import com.stsc.backend.model.Journey;
import com.stsc.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    List<Journey> findByUserOrderByStartedAtDesc(User user);
    Optional<Journey> findByUserAndStatus(User user, Journey.JourneyStatus status);
}
