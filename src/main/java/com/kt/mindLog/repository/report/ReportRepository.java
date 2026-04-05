package com.kt.mindLog.repository.report;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.report.Report;
import com.kt.mindLog.domain.report.ReportStatus;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

public interface ReportRepository extends JpaRepository<Report, UUID> {
	boolean existsByUserIdAndStatusAndPeriodStartAndPeriodEnd(UUID userId, ReportStatus status, LocalDate periodStart, LocalDate periodEnd);

	default Report findByIdOrThrow(UUID reportId, ErrorCode errorCode) {
		return findById(reportId).orElseThrow(() -> new CustomException(errorCode));
	}

	List<Report> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
