package com.kt.mindLog.dto.payment.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.domain.payment.PaymentStatus;

public record PaymentListResponse(
	int amount,
	String orderName,
	PaymentStatus status,
	LocalDateTime approvedAt,
	UUID paymentId
) {
	public static PaymentListResponse from(final Payment payment) {
		return new PaymentListResponse(
			payment.getAmount(),
			payment.getOrderName(),
			payment.getStatus(),
			payment.getApprovedAt(),
			payment.getId()
		);
	}
}
