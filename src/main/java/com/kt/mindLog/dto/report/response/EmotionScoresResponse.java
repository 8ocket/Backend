package com.kt.mindLog.dto.report.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record EmotionScoresResponse(
	@JsonProperty("session_id")
	String sessionId,

	@JsonProperty("score")
	Integer score,

	@JsonProperty("recorded_at")
	LocalDateTime recordedAt
) {
	public static EmotionScoresResponse from(Session session, Integer score) {
		return new EmotionScoresResponse(
			session.getId().toString(),
			score,
			session.getEndedAt()
		);
	}
}
