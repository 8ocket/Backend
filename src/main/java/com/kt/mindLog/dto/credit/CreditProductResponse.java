package com.kt.mindLog.dto.credit;

public record CreditProductResponse(
	String name,
	int creditAmount,
	int price
) {
	public static CreditProductResponse of(String name, int creditAmount, int price) {
		return new CreditProductResponse(name, creditAmount, price);
	}
}
