package com.kt.mindLog.dto.payment.response;

import java.time.OffsetDateTime;

public record TossPaymentResponse(
	String paymentKey,
	String orderId,
	int totalAmount,
	String status,
	OffsetDateTime approvedAt
) {
}
