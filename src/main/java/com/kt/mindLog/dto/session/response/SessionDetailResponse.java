package com.kt.mindLog.dto.session.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.Session;

public record SessionDetailResponse(
	@JsonProperty("session_id")
	String sessionId,

	@JsonProperty("persona_image_url")
	String personaImageUrl,

	@JsonProperty("persona_name")
	String personaName,

	String status,

	List<SessionMessageListResponse> messages,

	@JsonProperty("has_summary")
	boolean hasSummary
) {
	public static SessionDetailResponse from(Session session, List<SessionMessageListResponse> messages, boolean hasSummary) {
		return new SessionDetailResponse(
			session.getId().toString(),
			session.getPersona().getPersonaImageUrl(),
			session.getPersona().getPersonaName(),
			session.getStatus().toString(),
			messages,
			hasSummary
		);
	}
}
