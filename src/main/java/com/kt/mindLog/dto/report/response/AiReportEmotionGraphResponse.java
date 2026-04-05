package com.kt.mindLog.dto.report.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiReportEmotionGraphResponse(
	@JsonProperty("session_id")
	UUID sessionId,

	@JsonProperty("avg_score")
	Integer avgScore,

	@JsonProperty("is_inflection_point")
	boolean isInflectionPoint,

	@JsonProperty("inflection_type")
	String inflectionType,

	@JsonProperty("recorded_at")
	String recordedAt
) {
}
