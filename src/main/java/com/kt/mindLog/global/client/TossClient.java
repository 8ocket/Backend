package com.kt.mindLog.global.client;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TossClient {
	private final WebClient tossClient;

	public void refund(String paymentKey) {
		tossClient.post()
			.uri("/v1/payments/{paymentId}/cancel", paymentKey)
			.bodyValue(Map.of("cancelReason", "고객 환불 요청"))
			.retrieve()
			.bodyToMono(Void.class)
			.block();
	}
}
