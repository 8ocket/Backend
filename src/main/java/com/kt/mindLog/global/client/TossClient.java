package com.kt.mindLog.global.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.dto.payment.response.TossPaymentResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TossClient {
	private final WebClient tossClient;

	public TossClient(@Qualifier("tossWebClient") WebClient tossClient) {
		this.tossClient = tossClient;
	}

	public void refund(String paymentKey) {
		tossClient.post()
			.uri("/v1/payments/{paymentId}/cancel", paymentKey)
			.bodyValue(Map.of("cancelReason", "고객 환불 요청"))
			.retrieve()
			.onStatus(HttpStatusCode::isError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.flatMap(body -> {
						log.error("Toss refund error: status={}, body={}", clientResponse.statusCode(), body);
						return Mono.error(new CustomException(ErrorCode.TOSS_REFUND_FAILED));
					})
			)
			.bodyToMono(Void.class)
			.block();
	}

	public TossPaymentResponse confirm(String paymentKey, String orderId, int amount) {
		return tossClient.post()
			.uri("/v1/payments/confirm")
			.bodyValue(Map.of(
				"paymentKey", paymentKey,
				"orderId", orderId,
				"amount", amount
			))
			.retrieve()
			.onStatus(HttpStatusCode::isError, clientResponse ->
				clientResponse.bodyToMono(String.class)
					.flatMap(body -> {
						log.error("Toss confirm error: status={}, body={}", clientResponse.statusCode(), body);
						return Mono.error(new CustomException(ErrorCode.TOSS_CONFIRM_FAILED));
					})
			)
			.bodyToMono(TossPaymentResponse.class)
			.block();
	}
}
