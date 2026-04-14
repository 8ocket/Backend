package com.kt.mindLog.dto.payment.request;

public record PaymentConfirmRequest(
	String paymentKey,
	String orderId,
	int amount
) {
}
