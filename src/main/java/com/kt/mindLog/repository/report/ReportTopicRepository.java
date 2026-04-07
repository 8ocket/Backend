package com.kt.mindLog.repository.report;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.report.ReportTopic;

@Repository
public interface ReportTopicRepository extends JpaRepository<ReportTopic, UUID> {
	List<ReportTopic> findByReportId(UUID reportId);
}
