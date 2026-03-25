package com.kt.mindLog.service.redis;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.kt.mindLog.domain.summary.SessionContextSummary;
import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.dto.redis.request.RedisHistoryRequest;
import com.kt.mindLog.dto.redis.request.RedisMessageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String SESSION_PREFIX = "session:";
	private static final String MESSAGE_SUFFIX = ":messages";
	private static final String USER_SUFFIX = "user:";
	private static final String HISTORY_SUFFIX = ":recent_contexts";

	public int pushMessage(final UUID sessionId, final Role role, final String content) {
		String key = buildMessageKey(sessionId);

		RedisMessageRequest message = new RedisMessageRequest(role.toString().toLowerCase(), content);
		Long index = redisTemplate.opsForList().rightPush(key, objectMapper.writeValueAsString(message));
		log.info("success to push message : sessionId={}, content={}", sessionId, content);

		return index == null ? 1 : index.intValue();
	}

	private String buildMessageKey(final UUID sessionId) {
		return SESSION_PREFIX + sessionId + MESSAGE_SUFFIX;
	}

	public void pushHistory(final UUID userId, final SessionContextSummary summary) {
		String key = buildUserKey(userId);

		RedisHistoryRequest history = RedisHistoryRequest.from(summary);
		redisTemplate.opsForList().rightPush(key, objectMapper.writeValueAsString(history));
	}

	private String buildUserKey(UUID userId) {
		return USER_SUFFIX + userId + HISTORY_SUFFIX;
	}
}