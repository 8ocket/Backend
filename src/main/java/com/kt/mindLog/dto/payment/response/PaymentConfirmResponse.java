package com.kt.mindLog.dto.payment.response;

import java.time.OffsetDateTime;

public record PaymentConfirmResponse(
	String orderId,
	int amount,
	String status,
	OffsetDateTime approvedAt
) {
	public static PaymentConfirmResponse from(TossPaymentResponse response) {
		return new PaymentConfirmResponse(
			response.orderId(),
			response.totalAmount(),
			response.status(),
			response.approvedAt()
		);
	}
}
