package com.kt.mindLog.domain.credit;

import lombok.Getter;

@Getter
public enum ProductType {
	SMALL("소형", 200, 2200),
	MEDIUM("중형", 500, 4900),
	LARGE("대형", 1200, 10900);

	private final String title;
	private final int credit;
	private final int price;

	ProductType(String title, int credit, int price) {
		this.title = title;
		this.credit = credit;
		this.price = price;
	}
}
