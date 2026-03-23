package com.kt.mindLog.dto.session.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record ActiveSessionResponse(
	@JsonProperty("session_id")
	String sessionId,

	String title,

	@JsonProperty("started_at")
	LocalDateTime startedAt
) {
	public static ActiveSessionResponse from(Session session) {
		return new ActiveSessionResponse(
			session.getId().toString(),
			session.getTitle(),
			session.getStartedAt()
		);
	}
}
