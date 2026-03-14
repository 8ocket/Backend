package com.kt.mindLog.service.redis;

import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.kt.mindLog.domain.user.Role;
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

	public int pushMessage(UUID sessionId, Role role, String content) {
		String key = buildKey(sessionId);

		RedisMessageRequest message = new RedisMessageRequest(role.toString().toLowerCase(), content);
		Long index = redisTemplate.opsForList().rightPush(key, objectMapper.writeValueAsString(message));
		log.info("success to push message : sessionId={}, content={}", sessionId, content);

		return index == null ? 1 : index.intValue();
	}

	public List<RedisMessageRequest> getMessages(UUID sessionId) {
		String key = buildKey(sessionId);

		List<Object> list = redisTemplate.opsForList().range(key, 0, -1);

		if (list == null) return List.of();

		return list.stream()
			.map(o -> objectMapper.readValue((String) o, RedisMessageRequest.class))
			.toList();
	}

	private String buildKey(UUID sessionId) {
		return SESSION_PREFIX + sessionId + MESSAGE_SUFFIX;
	}

	private int executeOperation(Runnable operation) {
		try {
			operation.run();
			return 1;
		} catch (Exception e) {
			log.error("Redis 작업 오류: " + e.getMessage());
			return 0;
		}
	}
}