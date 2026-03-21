package com.kt.mindLog.dto.session.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record SessionListResponse(
	String sessionId,
	@JsonProperty("persona_image_url")
	String personaImageUrl,
	String title,
	String status,
	LocalDateTime startedAt
) {
	public static SessionListResponse from(Session session) {
		return new SessionListResponse(
			session.getId().toString(),
			session.getPersona().getPersonaImageUrl(),
			session.getTitle(),
			session.getStatus().toString(),
			session.getStartedAt()
		);
	}
}
