package com.kt.mindLog.service.session;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.session.response.FirstSessionResponse;
import com.kt.mindLog.dto.sessionMessage.response.CrisisCheck;
import com.kt.mindLog.dto.summary.response.SessionSummaryResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.global.property.SessionProperties;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.service.sse.SSEService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionStreamService {
	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;

	private final ObjectMapper objectMapper;
	private final SessionProperties sessionProperties;

	private final SSEService sseService;
	private final SessionMessageService messageService;

	//일반 메세지 통신
	public Flux<Object> receiveSSE(final String contents, final UUID sessionId, final UUID userId) {
		var user = validateUserAndSession(userId, sessionId);
		AtomicReference<UUID> messageIdRef = new AtomicReference<>();

		return sseService.streamSSE(sessionProperties.getMessageUri(), sessionId, Map.of("content", contents))
			.doFirst(() -> messageIdRef.set(messageService.saveContents(Role.USER, contents, sessionId)))
			.handle((event, sink) -> handleMessageEvent(event, sink, sessionId, user, messageIdRef))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void handleMessageEvent(final ServerSentEvent<String> event, final SynchronousSink<Object> sink,
		final UUID sessionId, final User user, final AtomicReference<UUID> messageIdRef) {
		switch (event.event()) {
			case "ai_chunk" -> sink.next(event);

			case "crisis_check" -> {
				CrisisCheck crisis = objectMapper.readValue(event.data(), CrisisCheck.class);
				if (crisis.level() >= 2) {
					sink.next(ServerSentEvent.builder(Map.of("content", crisis.suggestedResponse()))
						.event("crisis_check")
						.build());
					messageService.saveCrisis(sessionId, crisis, user, messageIdRef.get());
				}
			}

			case "ai_complete" -> {
				messageService.saveContents(Role.ASSISTANT, parseJson(event.data(), "content"), sessionId);
				sink.next(ServerSentEvent.builder().event("ai_complete").build());
			}

			case "error", "done" -> {
				sink.next(event);
				sink.complete();
			}
		}
	}


	//첫 메세지 통신
	public Flux<Object> receiveFirstMessage(final String contents, final UUID sessionId, final UUID userId) {
		validateUser(userId);
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		return sseService.streamSSE(sessionProperties.getMessageUri(), sessionId, Map.of("content", contents))
			.doFirst(() -> messageService.saveContents(Role.USER, contents, sessionId))
			.handle((event, sink) ->  handleFirstMessageEvent(event, sink, session))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void handleFirstMessageEvent(final ServerSentEvent<String> event, final SynchronousSink<Object> sink,
		final Session session) {
		switch (event.event()) {
			case "session_title" -> {
				messageService.saveTitle(session.getId(), event.data());
				sink.next(event);
			}

			case "ai_chunk" -> sink.next(event);

			case "ai_complete" -> {
				messageService.saveContents(
					Role.ASSISTANT,
					parseJson(event.data(), "content"),
					session.getId()
				);

				var firstResponse = FirstSessionResponse.from(session);

				sink.next(ServerSentEvent.builder()
					.event("ai_complete")
					.data(objectMapper.writeValueAsString(firstResponse))
					.build());
			}

			case "error", "done" -> {
				sink.next(event);
				sink.complete();
			}
		}
	}


	//세션 저장
	public Flux<Object> finalizeSession(final UUID sessionId, final UUID userId) {
		validateUser(userId);
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		if (session.getSummary() != null) {
			new CustomException(ErrorCode.INVALID_SESSION_SUMMARY).printStackTrace();
		}
		Preconditions.validate(session.getStatus().equals(SessionStatus.ACTIVE), ErrorCode.INVALID_SESSION);

		return sseService.streamSSE(sessionProperties.getFinalizeUri(), sessionId, null)
			.handle((event, sink) -> handleFinalizeEvent(event, sink, sessionId))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void handleFinalizeEvent(final ServerSentEvent<String> event, final SynchronousSink<Object> sink, final UUID sessionId) {
		switch (event.event()) {
			case "status" -> sink.next(event);

			case "session_title" -> messageService.saveTitle(sessionId, event.data());

			case "ai_complete" -> {
				var summary = objectMapper.readValue(event.data(), SessionSummaryResponse.class);

				List<Map<String, Object>> emotions = summary.emotions().stream()
					.map(r -> Map.<String, Object>of(
						"emotion_type", r.emotionType(),
						"intensity", r.intensity(),
						"source_keyword", r.sourceKeyword()
					))
					.toList();

				sink.next(ServerSentEvent.builder()
					.event("ai_complete")
					.data(Map.of(
						"session_id", sessionId.toString(),
						"summary", summary.summary(),
						"emotions", emotions,
						"card_image_url", summary.card().get("image_url").asText()
					))
					.build());

				String imageUrl = summary.card().get("image_url").asText();

				messageService.saveSessionSummary(sessionId, summary, imageUrl);
			}
		}
	}

	private String parseJson(final String contents, final String key) {
		return objectMapper.readTree(contents).get(key).asText();
	}

	private User validateUser(final UUID userId) {
		return userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
	}

	private User validateUserAndSession(final UUID userId, final UUID sessionId) {
		var user = validateUser(userId);
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);
		Preconditions.validate(session.getStatus().equals(SessionStatus.ACTIVE), ErrorCode.INVALID_SESSION);
		return user;
	}
}
