package com.kt.mindLog.dto.redis.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.mindLog.domain.summary.SessionContextSummary;

public record RedisHistoryRequest(
	@JsonProperty("session_id")
	UUID sessionId,
	String content,
	@JsonProperty("created_at")
	String createdAt
) {
	public static RedisHistoryRequest from(SessionContextSummary summary, String decryptContent) {
		return new RedisHistoryRequest(
			summary.getSession().getId(),
			decryptContent,
			summary.getCreatedAt().toString()
		);
	}
}
