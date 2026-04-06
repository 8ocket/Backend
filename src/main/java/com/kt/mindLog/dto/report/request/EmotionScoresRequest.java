package com.kt.mindLog.dto.report.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record EmotionScoresRequest(
	@JsonProperty("session_id")
	String sessionId,

	@JsonProperty("score")
	Integer score,

	@JsonProperty("recorded_at")
	LocalDateTime recordedAt
) {
	public static EmotionScoresRequest from(Session session, Integer score) {
		return new EmotionScoresRequest(
			session.getId().toString(),
			score,
			session.getEndedAt()
		);
	}
}
