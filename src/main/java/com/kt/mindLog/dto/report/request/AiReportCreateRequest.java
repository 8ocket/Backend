package com.kt.mindLog.dto.report.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.dto.report.response.EmotionScoresResponse;

import lombok.Builder;

@Builder
public record AiReportCreateRequest(
	@JsonProperty("user_id")
	String userId,

	@JsonProperty("report_type")
	String reportType,

	@JsonProperty("period_start")
	LocalDate periodStart,

	@JsonProperty("period_end")
	LocalDate periodEnd,

	@JsonProperty("emotion_scores")
	List<EmotionScoresResponse> emotionScores
) {
}
