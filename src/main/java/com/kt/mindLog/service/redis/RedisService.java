package com.kt.mindLog.service.redis;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.kt.mindLog.domain.enums.Role;
import com.kt.mindLog.dto.redis.RedisMessageRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	private static final String SESSION_PREFIX = "session:";
	private static final String MESSAGE_SUFFIX = ":messages";

	/**
	 * session message 저장
	 */
	public int pushMessage(String sessionId, Role role, String content) {

		String key = buildKey(sessionId);

		RedisMessageRequest message = new RedisMessageRequest(role.toString().toLowerCase(), content);

		return executeOperation(() ->
			redisTemplate.opsForList().rightPush(key, message)
		);
	}

	/**
	 * session message 조회
	 */
	public List<RedisMessageRequest> getMessages(String sessionId) {

		String key = buildKey(sessionId);

		List<Object> list = redisTemplate.opsForList().range(key, 0, -1);

		if (list == null) return List.of();

		return list.stream()
			.map(o -> (RedisMessageRequest) o)
			.toList();
	}

	/**
	 * key 생성
	 */
	private String buildKey(String sessionId) {
		return SESSION_PREFIX + sessionId + MESSAGE_SUFFIX;
	}

	/**
	 * 공통 Redis 실행
	 */
	private int executeOperation(Runnable operation) {
		try {
			operation.run();
			return 1;
		} catch (Exception e) {
			System.out.println("Redis 작업 오류 :: " + e.getMessage());
			return 0;
		}
	}
}