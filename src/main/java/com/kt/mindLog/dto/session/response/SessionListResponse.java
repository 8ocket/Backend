package com.kt.mindLog.dto.session.response;

import java.time.LocalDateTime;

import com.kt.mindLog.domain.session.Session;

public record SessionListResponse(
	String sessionId,
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
