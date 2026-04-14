package com.kt.mindLog.dto.redis.request;

public record RedisMessageRequest(
	String role,
	String content
) {
}
