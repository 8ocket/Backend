package com.kt.mindLog.repository.report;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.report.ReportSuggestion;

@Repository
public interface ReportSuggestionRepository extends JpaRepository<ReportSuggestion, UUID> {
	List<ReportSuggestion> findByReportIdOrderByPriorityAsc(UUID reportId);

	void deleteByReportId(UUID reportId);
}
