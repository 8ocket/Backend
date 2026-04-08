package com.kt.mindLog.repository.report;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.report.ReportAnalysis;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Repository
public interface ReportAnalysisRepository extends JpaRepository<ReportAnalysis, UUID> {
	Optional<ReportAnalysis> findByReportId(UUID reportId);

	default ReportAnalysis findByReportIdOrThrow(UUID reportId, ErrorCode errorCode) {
		return findByReportId(reportId).orElseThrow(() -> new CustomException(errorCode));
	}

	void deleteByReportId(UUID reportId);
}
