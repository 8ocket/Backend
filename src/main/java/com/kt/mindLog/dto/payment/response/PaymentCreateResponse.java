package com.kt.mindLog.dto.payment.response;

public record PaymentCreateResponse(
	String orderId,
	int amount,
	String orderName
) {
	public static PaymentCreateResponse of(String orderId, int amount, String orderName) {
		return new PaymentCreateResponse(orderId, amount, orderName);
	}
}
