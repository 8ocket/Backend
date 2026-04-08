package com.kt.mindLog.dto.sessionMessage.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.session.SessionMessages;

public record SessionMessageListResponse(
	@JsonProperty("message_id")
	String messageId,

	String role,

	String content,

	@JsonProperty("sequence_num")
	int sequenceNum,

	@JsonProperty("created_at")
	LocalDateTime createdAt
) {
	public static SessionMessageListResponse from(SessionMessages messages, String decryptContent) {
		return new SessionMessageListResponse(
			messages.getId().toString(),
			messages.getRole().toString(),
			decryptContent,
			messages.getSequenceNum(),
			messages.getCreatedAt()
		);
	}
}
