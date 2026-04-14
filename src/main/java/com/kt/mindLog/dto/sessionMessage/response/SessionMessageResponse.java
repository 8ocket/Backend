package com.kt.mindLog.dto.sessionMessage.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.SessionMessages;

public record SessionMessageResponse(
	@JsonProperty("message_id")
	String messageId,
	String content,
	@JsonProperty("created_at")
	LocalDateTime createdAt
) {
	public static SessionMessageResponse from(SessionMessages messages) {
		return new SessionMessageResponse(
			messages.getId().toString(),
			messages.getContent(),
			messages.getCreatedAt()
		);
	}
}
