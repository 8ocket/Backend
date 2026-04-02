package com.kt.mindLog.repository.report;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.report.Report;

public interface ReportRepository extends JpaRepository<Report, UUID> {
	boolean existsByUserIdAndPeriodStartAndPeriodEnd(UUID userId, LocalDate periodStart, LocalDate periodEnd);
}
