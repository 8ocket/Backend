package com.kt.mindLog.dto.payment.response;

import java.time.LocalDateTime;

public record TossPaymentResponse(
	String paymentKey,
	String orderId,
	int totalAmount,
	String status,
	LocalDateTime approvedAt
) {
}
