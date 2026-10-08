package com.stsc.backend.repository;

import com.stsc.backend.model.TravelMemory;
import com.stsc.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelMemoryRepository extends JpaRepository<TravelMemory, Long> {
    List<TravelMemory> findByUserOrderByVisitedAtDesc(User user);
}
