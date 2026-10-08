package com.stsc.backend.service;

import com.stsc.backend.config.AppConstants;
import com.stsc.backend.model.IncidentReport;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Calculates a simple Green / Yellow / Red indicator for a location, based
 * entirely on nearby ACTIVE citizen reports - their freshness, net
 * confirmations, and distance. This is the scoped-down replacement for
 * "real crowd sensing from live multi-user GPS", which is not feasible to
 * demo without a real user base.
 */
@Service
public class SafetyColorService {

    public enum SafetyColor { GREEN, YELLOW, RED }

    /**
     * @param nearbyActiveReports reports already filtered to ACTIVE status and
     *                            within the proximity radius of the point being checked
     */
    public SafetyColor calculate(List<IncidentReport> nearbyActiveReports) {
        int weightedScore = 0;

        for (IncidentReport report : nearbyActiveReports) {
            int netConfirms = report.getConfirmCount() - report.getDismissCount();
            if (netConfirms <= 0) continue;

            boolean isFresh = Duration.between(report.getCreatedAt(), LocalDateTime.now()).toHours()
                    <= AppConstants.REPORT_FRESH_HOURS;

            // Fresh + confirmed reports weigh more than stale ones
            weightedScore += isFresh ? netConfirms * 2 : netConfirms;
        }

        if (weightedScore >= AppConstants.RED_CONFIRM_THRESHOLD * 2) {
            return SafetyColor.RED;
        } else if (weightedScore >= AppConstants.YELLOW_CONFIRM_THRESHOLD) {
            return SafetyColor.YELLOW;
        }
        return SafetyColor.GREEN;
    }
}
