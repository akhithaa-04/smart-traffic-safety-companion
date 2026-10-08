package com.stsc.backend.controller;

import com.stsc.backend.model.Badge;
import com.stsc.backend.model.User;
import com.stsc.backend.model.UserBadge;
import com.stsc.backend.repository.BadgeRepository;
import com.stsc.backend.repository.UserBadgeRepository;
import com.stsc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BadgeController {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserService userService;

    @GetMapping
    public List<Badge> allBadges() {
        return badgeRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<UserBadge> userBadges(@PathVariable Long userId) {
        User user = userService.getById(userId);
        return userBadgeRepository.findByUser(user);
    }
}
