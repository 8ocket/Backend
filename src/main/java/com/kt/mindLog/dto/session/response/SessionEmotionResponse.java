package com.kt.mindLog.dto.session.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SessionEmotionResponse(
	@JsonProperty("emotion_type")
	String emotionType,

	Integer intensity,

	@JsonProperty("source_keyword")
	String sourceKeyword,

	@JsonProperty("trigger_context")
	String triggerContext,

	@JsonProperty("emotion_trajectory")
	String emotionTrajectory
) {
}