package com.kt.mindLog.service.session;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

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
import com.kt.mindLog.global.property.StreamProperties;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.session.SessionRepository;
import com.kt.mindLog.service.credit.CreditService;
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
	private final StreamProperties streamProperties;

	private final SSEService sseService;
	private final SessionMessageService messageService;
	private final CreditService creditService;

	//일반 메세지 통신
	public Flux<Object> receiveSSE(final String contents, final UUID sessionId, final UUID userId) {
		var user = validateUserAndSession(userId, sessionId);
		AtomicReference<UUID> messageIdRef = new AtomicReference<>();

		return sseService.streamSSE(streamProperties.getMessageUri(), sessionId, Map.of("content", contents))
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

		return sseService.streamSSE(streamProperties.getMessageUri(), sessionId, Map.of("content", contents))
			.doFirst(() -> messageService.saveContents(Role.USER, contents, sessionId))
			.handle((event, sink) ->  handleFirstMessageEvent(event, sink, session, userId))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void handleFirstMessageEvent(final ServerSentEvent<String> event, final SynchronousSink<Object> sink,
		final Session session, UUID userId) {
		switch (event.event()) {
			case "session_title" -> {
				messageService.saveTitle(session.getId(), event.data());
				sink.next(event);
			}

			case "ai_chunk" -> sink.next(event);

			case "ai_complete" -> {

				try {
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

					creditService.earnCreditForSession(userId, session.getId());
				} catch (Exception e) {
					log.error("세션 생성 실패 | sessionId={}", session.getId(), e);

					sink.next(ServerSentEvent.builder()
						.event("server_error")
						.data(Map.of(
							"content", "failed to create session"
						))
						.build());
				} finally {
					sink.complete();
				}
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

		return sseService.streamSSE(streamProperties.getFinalizeUri(), sessionId, null)
			.handle((event, sink) -> handleFinalizeEvent(event, sink, sessionId, userId))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void handleFinalizeEvent(final ServerSentEvent<String> event, final SynchronousSink<Object> sink,
		final UUID sessionId, final UUID userId) {
		switch (event.event()) {
			case "status" -> sink.next(event);

			case "session_title" -> messageService.saveTitle(sessionId, event.data());

			case "ai_complete" -> {

				try {
					var summary = objectMapper.readValue(event.data(), SessionSummaryResponse.class);

					List<Map<String, Object>> emotions = summary.emotions().stream()
						.map(r -> Map.<String, Object>of(
							"emotion_type", r.emotionType(),
							"intensity", r.intensity(),
							"source_keyword", r.sourceKeyword()
						))
						.toList();

					String imageUrl = summary.card().get("image_url").asText();

					var summaryId = messageService.saveSessionSummary(sessionId, summary, imageUrl, userId);

					sink.next(ServerSentEvent.builder()
						.event("ai_complete")
						.data(Map.of(
							"session_id", sessionId.toString(),
							"summary_id", summaryId,
							"summary", summary.summary(),
							"emotions", emotions,
							"card_image_url", summary.card().get("image_url").asText()
						))
						.build());
				} catch (Exception e) {
					log.error("마음기록카드 생성 실패 | sessionId={}", sessionId, e);

					sink.next(ServerSentEvent.builder()
						.event("server_error")
						.data(Map.of(
							"content", "failed to create report"
						))
						.build());
				} finally {
					sink.complete();
				}

			}

			case "done" -> sink.complete();
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
