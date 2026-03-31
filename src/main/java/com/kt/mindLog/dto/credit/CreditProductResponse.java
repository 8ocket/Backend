package com.kt.mindLog.dto.credit;

import java.util.List;

public record CreditProductResponse(
	String name,
	int creditAmount,
	int price
) {
	public static CreditProductResponse of(String name, int creditAmount, int price) {
		return new CreditProductResponse(name, creditAmount, price);
	}
}
