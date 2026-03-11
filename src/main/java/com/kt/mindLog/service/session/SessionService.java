package com.kt.mindLog.service.session;

import java.time.Duration;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.dto.session.response.CrisisCheck;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.property.SessionProperties;
import com.kt.mindLog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final SessionProperties sessionProperties;
	private final WebClient webClient;


	public Flux<ServerSentEvent<?>> receiveSSE(final String contents, final Long sessionId, final Long userId) {
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
			.doOnNext(event -> {
				if (!"error".equals(event.event())) {
					log.info("event: {}, data: {}", event.event(), event.data());
				}})
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

			case "error" -> {
				log.error("AI server error: {}", event.data());
				sink.next(event);
				sink.complete();
			}

			case "ai_complete" -> {
				sink.next(ServerSentEvent.builder()
					.event("ai_complete")
					.build());
				//TODO db 저장 로직
			}

			case "done" -> sink.complete();

			default -> {}
		}
	}
}
