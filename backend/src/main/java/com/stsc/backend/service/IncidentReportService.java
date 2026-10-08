package com.stsc.backend.service;

import com.stsc.backend.config.AppConstants;
import com.stsc.backend.dto.ConfirmDismissRequest;
import com.stsc.backend.dto.IncidentReportRequest;
import com.stsc.backend.model.IncidentReport;
import com.stsc.backend.model.ReportConfirmation;
import com.stsc.backend.model.User;
import com.stsc.backend.repository.IncidentReportRepository;
import com.stsc.backend.repository.ReportConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentReportService {

    private final IncidentReportRepository reportRepository;
    private final ReportConfirmationRepository confirmationRepository;
    private final UserService userService;
    private final RewardService rewardService;
    private final SafetyColorService safetyColorService;

    /** Any citizen can submit a report at any time - whether or not they are on an active journey. */
    public IncidentReport submitReport(IncidentReportRequest request) {
        User reporter = userService.getById(request.getReporterId());

        IncidentReport report = IncidentReport.builder()
                .reporter(reporter)
                .type(request.getType())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .description(request.getDescription())
                .cause(request.getCause())
                .photoUrl(request.getPhotoUrl())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .suggestedAlternateRoute(request.getSuggestedAlternateRoute())
                .build();

        report = reportRepository.save(report);

        int points = AppConstants.POINTS_NEW_REPORT
                + (request.getPhotoUrl() != null && !request.getPhotoUrl().isBlank()
                    ? AppConstants.POINTS_REPORT_WITH_PHOTO_BONUS : 0);
        rewardService.awardPoints(reporter, points);

        return report;
    }

    /** Confirm or dismiss an existing report; keeps the data layer current via community input. */
    public IncidentReport confirmOrDismiss(Long reportId, ConfirmDismissRequest request) {
        IncidentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
        User user = userService.getById(request.getUserId());

        confirmationRepository.findByReportAndUser(report, user).ifPresent(existing -> {
            throw new IllegalStateException("You have already responded to this report.");
        });

        confirmationRepository.save(ReportConfirmation.builder()
                .report(report)
                .user(user)
                .actionType(request.getActionType())
                .build());

        if (request.getActionType() == ReportConfirmation.ActionType.CONFIRM) {
            report.setConfirmCount(report.getConfirmCount() + 1);
            report.setLastConfirmedAt(LocalDateTime.now());
            rewardService.awardPoints(user, AppConstants.POINTS_CONFIRM_VALID_REPORT);
        } else {
            report.setDismissCount(report.getDismissCount() + 1);
            rewardService.awardPoints(user, AppConstants.POINTS_FLAG_CLEARED_REPORT);

            // If dismissals clearly outweigh confirms, mark the report cleared
            if (report.getDismissCount() - report.getConfirmCount() >= 2) {
                report.setStatus(IncidentReport.ReportStatus.CLEARED);
            }
        }

        return reportRepository.save(report);
    }

    /** Lazily expires stale, unconfirmed ACTIVE reports. Call before serving nearby/active lists. */
    public void expireStaleReports() {
        List<IncidentReport> active = reportRepository.findByStatusOrderByCreatedAtDesc(IncidentReport.ReportStatus.ACTIVE);
        for (IncidentReport report : active) {
            long hoursOld = Duration.between(report.getCreatedAt(), LocalDateTime.now()).toHours();
            if (hoursOld >= AppConstants.REPORT_EXPIRE_HOURS) {
                report.setStatus(IncidentReport.ReportStatus.EXPIRED);
                reportRepository.save(report);
            }
        }
    }

    /** Reports within radiusMeters of (lat, lng), ACTIVE status only - the "relevant to this journey" check. */
    public List<IncidentReport> findNearbyActive(double lat, double lng, double radiusMeters) {
        expireStaleReports();
        return reportRepository.findByStatusOrderByCreatedAtDesc(IncidentReport.ReportStatus.ACTIVE).stream()
                .filter(r -> GeoUtil.distanceMeters(lat, lng, r.getLatitude(), r.getLongitude()) <= radiusMeters)
                .collect(Collectors.toList());
    }

    public SafetyColorService.SafetyColor safetyColorFor(double lat, double lng, double radiusMeters) {
        return safetyColorService.calculate(findNearbyActive(lat, lng, radiusMeters));
    }

    public List<IncidentReport> findAllActive() {
        expireStaleReports();
        return reportRepository.findByStatusOrderByCreatedAtDesc(IncidentReport.ReportStatus.ACTIVE);
    }
}
