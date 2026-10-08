package com.stsc.backend.repository;

import com.stsc.backend.model.IncidentReport;
import com.stsc.backend.model.ReportConfirmation;
import com.stsc.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportConfirmationRepository extends JpaRepository<ReportConfirmation, Long> {
    Optional<ReportConfirmation> findByReportAndUser(IncidentReport report, User user);
    long countByReportAndActionType(IncidentReport report, ReportConfirmation.ActionType actionType);
    long countByUserAndActionType(User user, ReportConfirmation.ActionType actionType);
}
