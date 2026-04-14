package com.kt.mindLog.dto.session.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.dto.sessionMessage.response.SessionMessageResponse;

public record SessionResponse (
	@JsonProperty("session_id")
	String sessionId,
	String status,
	String title,
	@JsonProperty("started_at")
	LocalDateTime startedAt,
	@JsonProperty("first_message")
	SessionMessageResponse firstMessage
) {
	public static SessionResponse from(Session session, SessionMessageResponse messageResponse) {
		return new SessionResponse(
			session.getId().toString(),
			session.getStatus().toString(),
			session.getTitle(),
			session.getStartedAt(),
			messageResponse
		);
	}
}
