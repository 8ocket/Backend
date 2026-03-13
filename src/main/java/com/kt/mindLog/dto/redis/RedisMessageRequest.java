package com.kt.mindLog.dto.redis;

public record RedisMessageRequest(
	String role,
	String content
) {
}
