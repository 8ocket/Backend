package com.kt.mindLog.dto.report.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.report.Report;

public record ReportResponse(
	@JsonProperty("report_id")
	UUID reportId,

	@JsonProperty("report_type")
	String reportType,

	@JsonProperty("period_start")
	LocalDate periodStart,

	@JsonProperty("period_end")
	LocalDate periodEnd,

	String status,

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	@JsonProperty("is_viewed")
	boolean isViewed
) {
	public static ReportResponse from(Report report) {
		return new ReportResponse(
			report.getId(),
			report.getReportType().toString(),
			report.getPeriodStart(),
			report.getPeriodEnd(),
			report.getStatus().toString(),
			report.getCreatedAt(),
			report.isViewed()
		);
	}
}