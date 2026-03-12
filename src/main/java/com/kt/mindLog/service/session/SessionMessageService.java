package com.kt.mindLog.service.session;

import java.time.Duration;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.domain.session.SessionMessage;
import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.dto.session.response.CrisisCheck;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.property.SessionProperties;
import com.kt.mindLog.repository.SessionMessageRepository;
import com.kt.mindLog.repository.SessionRepository;
import com.kt.mindLog.repository.UserRepository;

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
	private final ObjectMapper objectMapper;
	private final SessionProperties sessionProperties;
	private final WebClient webClient;
	private final SessionMessageRepository sessionMessageRepository;
	private final SessionRepository sessionRepository;


	public Flux<ServerSentEvent<?>> receiveSSE(final String contents, final String sessionId, final Long userId) {
		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		return webClient.post()
			.uri(sessionProperties.getUri(), sessionId)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(contents)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(String.class)
					.map(body -> new RuntimeException("AI 서버 오류: " + body))
			)
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
			.timeout(Duration.ofMinutes(2))
			.doFirst(() -> saveContents(Role.USER, contents, sessionId))
			.doOnNext(event -> {
				if ("error".equals(event.event())) {
					log.error("AI error: {}", event.data());
				} else if ("ai_complete".equals(event.event())) {
					var content = parseJson(event.data(), "content");
					log.info("content: {}", content);
					saveContents(Role.ASSISTANT, content, sessionId);
				} else {
					log.info("event: {}, data: {}", event.event(), event.data());
				}
			})
			.handle(this::handleEvent)
			.doOnError(e -> log.error("스트림 오류", e));
	}


	private void handleEvent(ServerSentEvent<String> event, SynchronousSink<ServerSentEvent<?>> sink) {
		switch (event.event()) {

			case "ai_chunk" -> sink.next(event);

			case "crisis_check" -> {
				CrisisCheck crisis = objectMapper.readValue(event.data(), CrisisCheck.class);

				if(crisis.detected()) {
					sink.next(
						ServerSentEvent.builder(Map.of("content", crisis.suggestedResponse()))
							.event("crisis_check")
							.build()
					);
				}
			}

			case "error", "done" -> {
				sink.next(event);
				sink.complete();
			}

			case "ai_complete" -> {
				sink.next(ServerSentEvent.builder()
					.event("ai_complete")
					.build());
			}

			case "session_title" -> sink.next(event);

			default -> {}
		}
	}

	private String parseJson(final String contents, final String text) {
		var object = objectMapper.readTree(contents);
		String data = object.get(text).asText();
		return data;
	}

	@Transactional
	protected void saveContents(final Role role, final String contents, final String sessionId) {
		var session = sessionRepository.findByIdOrThrow(sessionId, ErrorCode.NOT_FOUND_SESSION);

		//TODO content 암호화
		//TODO redis 저장

		var message = SessionMessage.builder()
			.role(role)
			.content(contents)
			.session(session)
			.build();

		sessionMessageRepository.save(message);
		log.info("success to save sessionId: {}", sessionId);
	}
}
