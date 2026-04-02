package com.kt.mindLog.dto.report.response;

import java.util.UUID;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportEmotionGraphResponse(
	@JsonProperty("session_id")
	UUID sessionId,

	@JsonProperty("avg_score")
	Integer avgScore,

	@JsonProperty("is_inflection_point")
	boolean isInflectionPoint,

	@JsonProperty("inflection_type")
	String inflectionType,

	@JsonProperty("recorded_at")
	DateTime recordedAt
) {
}
