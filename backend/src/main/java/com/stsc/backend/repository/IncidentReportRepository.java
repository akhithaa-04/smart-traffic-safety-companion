package com.stsc.backend.repository;

import com.stsc.backend.model.IncidentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {
    List<IncidentReport> findByStatusOrderByCreatedAtDesc(IncidentReport.ReportStatus status);
}
