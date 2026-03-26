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
	public static RedisHistoryRequest from(SessionContextSummary summary) {
		return new RedisHistoryRequest(
			summary.getSession().getId(),
			summary.getContent(),
			summary.getCreatedAt().toString()
		);
	}
}
