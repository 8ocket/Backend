package com.kt.mindLog.service.session;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.domain.session.SessionMessages;
import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.dto.session.response.CrisisCheck;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.property.SessionProperties;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.service.redis.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionMessageService {

	private final UserRepository userRepository;
	private final SessionMessageRepository sessionMessageRepository;
	private final SessionRepository sessionRepository;

	private final ObjectMapper objectMapper;
	private final WebClient webClient;
	private final SessionProperties sessionProperties;

	private final RedisService redisService;


	public Flux<Object> receiveSSE(final String contents, final UUID sessionId, final UUID userId) {
		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		return webClient.post()
			.uri(sessionProperties.getUri(), sessionId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(Map.of("content", contents))
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(String.class)
					.map(body -> new RuntimeException("AI 서버 오류: " + body))
			)
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
			.timeout(Duration.ofMinutes(2))
			.doFirst(() -> saveContents(Role.USER, contents, sessionId))
			.doOnNext(this::logEvent)
			.handle((event, sink) -> handleEvent(event, sink, sessionId))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void logEvent(ServerSentEvent<String> event) {
		if ("error".equals(event.event())) {
			log.error("AI error: {}", event.data());
		} else {
			log.info("event: {}, data: {}", event.event(), event.data());
		}
	}

	private void handleEvent(ServerSentEvent<String> event, SynchronousSink<Object> sink, UUID sessionId) {
		switch (event.event()) {
			case "ai_chunk" -> sink.next(event);

			case "crisis_check" -> {
				CrisisCheck crisis = objectMapper.readValue(event.data(), CrisisCheck.class);
				if (crisis.detected()) {
					sink.next(ServerSentEvent.builder(Map.of("content", crisis.suggestedResponse()))
						.event("crisis_check")
						.build());
				}
			}

			case "ai_complete" -> {
				saveContents(Role.ASSISTANT, parseJson(event.data(), "content"), sessionId);
				sink.next(ServerSentEvent.builder().event("ai_complete").build());
			}

			case "error", "done" -> {
				sink.next(event);
				sink.complete();
			}
		}
	}

	private String parseJson(final String contents, final String key) {
		return objectMapper.readTree(contents).get(key).asText();
	}

	@Transactional
	protected UUID saveContents(final Role role, final String contents, final UUID sessionId) {

		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);
		int sequence = redisService.pushMessage(sessionId, role, contents);
		//TODO 상담 메세지 암호화

		var message = sessionMessageRepository.saveAndFlush(SessionMessages.builder()
			.role(role)
			.content(contents)
			.session(session)
			.sequenceNum(sequence)
			.build());

		log.info("success to save session message");
		return message.getId();
	}


	public UUID receiveFirstMessage(final String contents, final UUID sessionId, final UUID userId) {
		AtomicReference<UUID> messageIdRef = new AtomicReference<>();

		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		webClient.post()
			.uri(sessionProperties.getUri(), sessionId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(Map.of("content", contents))
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(String.class)

					.map(body -> new RuntimeException("AI 서버 오류: " + body))
			)
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
			.timeout(Duration.ofMinutes(2))
			.doFirst(() -> saveContents(Role.USER, contents, sessionId))
			.doOnNext(this::logEvent)
			.doOnNext(event -> {
				switch (event.event()) {
					case "session_title" -> saveTitle(sessionId, event.data());

					case "ai_complete" -> {
						UUID id = saveContents(
							Role.ASSISTANT,
							parseJson(event.data(), "content"),
							sessionId
						);
						messageIdRef.set(id);
					}
				}
			})
			.doOnError(e -> log.error("스트림 오류", e))
			.takeUntil(event -> "done".equals(event.event()))
			.blockLast();

		return messageIdRef.get();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveTitle(UUID sessionId, String contents) {

		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);
		String title = parseJson(contents, "title");
		session.updateTitle(title);

		sessionRepository.saveAndFlush(session);
		log.info("success to save session title");
	}
}
