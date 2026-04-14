package com.kt.mindLog.dto.report.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.report.ReportType;

import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest(
	@JsonProperty("report_type")
	@NotNull(message = "AI 리포트 타입은 필수 선택 사항입니다")
	ReportType reportType,

	@JsonProperty("period_start")
	@NotNull(message = "분석 시작 일자 입력은 필수 선택 사항입니다")
	LocalDate periodStart,

	@JsonProperty("period_end")
	@NotNull(message = "분석 마감 일자 입력은 필수 기입 사항입니다")
	LocalDate periodEnd
) {
}
