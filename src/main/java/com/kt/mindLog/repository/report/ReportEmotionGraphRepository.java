package com.kt.mindLog.repository.report;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.report.ReportEmotionGraph;

@Repository
public interface ReportEmotionGraphRepository extends JpaRepository<ReportEmotionGraph, UUID> {
	List<ReportEmotionGraph> findByReportId(UUID reportId);

	void deleteByReportId(UUID reportId);
}
