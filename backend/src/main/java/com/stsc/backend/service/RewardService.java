package com.stsc.backend.service;

import com.stsc.backend.model.Badge;
import com.stsc.backend.model.User;
import com.stsc.backend.model.UserBadge;
import com.stsc.backend.repository.BadgeRepository;
import com.stsc.backend.repository.UserBadgeRepository;
import com.stsc.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    /** Add points to a user, then re-check whether any new badge is earned. */
    public void awardPoints(User user, int points) {
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);
        checkAndAwardBadges(user);
    }

    public void registerSafeStart(User user) {
        user.setSafeStartStreak(user.getSafeStartStreak() + 1);
        awardPoints(user, com.stsc.backend.config.AppConstants.POINTS_SAFE_START);
    }

    public void resetSafeStartStreak(User user) {
        user.setSafeStartStreak(0);
        userRepository.save(user);
    }

    private void checkAndAwardBadges(User user) {
        List<Badge> allBadges = badgeRepository.findAll();
        for (Badge badge : allBadges) {
            if (userBadgeRepository.existsByUserAndBadge_Id(user, badge.getId())) continue;

            boolean earned = switch (badge.getCriteriaType()) {
                case POINTS -> user.getPoints() >= badge.getThreshold();
                case SAFE_START_STREAK -> user.getSafeStartStreak() >= badge.getThreshold();
                case CONFIRMED_REPORT_COUNT -> false; // evaluated separately where confirm counts are tracked
            };

            if (earned) {
                userBadgeRepository.save(UserBadge.builder().user(user).badge(badge).build());
            }
        }
    }
}
