package com.kt.mindLog.dto.report.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.report.ReportAnalysis;

public record TendencyResponse(
	@JsonProperty("current_status")
	String currentStatus,
	String tendency
) {
}
