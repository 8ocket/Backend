package com.kt.mindLog.dto.session.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SessionProgressResponse(
	@JsonProperty("report_type")
	String reportType,

	@JsonProperty("current_count")
	Integer currentCount,

	@JsonProperty("required_count")
	Integer requiredCount,

	@JsonProperty("progress_percentage")
	Integer progressPercentage
) {
}
