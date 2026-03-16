package com.kt.mindLog.dto.session.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record SessionResponse (
	@JsonProperty("session_id")
	String sessionId,
	@JsonProperty("persona_id")
	String personaId,
	String status,
	@JsonProperty("started_at")
	LocalDateTime startedAt,
	@JsonProperty("first_message")
	SessionMessageResponse firstMessage
) {
	public static SessionResponse from(Session session, SessionMessageResponse messageResponse) {
		return new SessionResponse(
			session.getId().toString(),
			session.getPersona().getId().toString(),
			session.getStatus().toString(),
			session.getStartedAt(),
			messageResponse
		);
	}
}
