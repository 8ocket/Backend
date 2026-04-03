package com.kt.mindLog.dto.session.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record FirstSessionResponse(
	@JsonProperty("session_id")
	String sessionId,

	String status,

	@JsonProperty("started_at")
	LocalDateTime startedAt
) {
	public static FirstSessionResponse from(Session session) {
		return new FirstSessionResponse(
			session.getId().toString(),
			session.getStatus().toString(),
			session.getStartedAt()
		);
	}
}
