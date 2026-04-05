package com.kt.mindLog.service.sse;

import java.time.Duration;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSEService {

	private final WebClient webClient;

	public Flux<ServerSentEvent<String>> streamSSE(final String uri, final UUID sessionId, final Object body) {
		var request = webClient.post()
			.uri(uri, sessionId)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.TEXT_EVENT_STREAM);

		if (body != null) {
			request = (WebClient.RequestBodySpec)request.bodyValue(body);
		}

		return request
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(String.class)
					.map(bodyStr -> new RuntimeException("AI 서버 오류: " + bodyStr))
			)
			.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
			.timeout(Duration.ofMinutes(5))
			.doOnNext(this::logEvent);
	}

	private void logEvent(final ServerSentEvent<String> event) {
		if ("error".equals(event.event())) {
			log.error("AI error: {}", event.data());
		} else {
			log.info("event: {}, data: {}", event.event(), event.data()); // TODO 최종 배포 시 삭제
		}
	}
}
