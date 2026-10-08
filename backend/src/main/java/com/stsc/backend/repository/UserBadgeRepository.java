package com.stsc.backend.repository;

import com.stsc.backend.model.User;
import com.stsc.backend.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(User user);
    boolean existsByUserAndBadge_Id(User user, Long badgeId);
}
