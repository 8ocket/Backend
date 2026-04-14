package com.kt.mindLog.domain.report;

import com.kt.mindLog.domain.credit.TransactionType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum ReportType {
	WEEKLY(7, 2, 150, TransactionType.AI_WEEKLY_REPORT),
	MONTHLY(31, 4, 500, TransactionType.AI_MONTHLY_REPORT);

	private final int maxDays;
	private final int minSessions;
	private final int amount;
	private final TransactionType transactionType;

	ReportType(int maxDays, int minSessions, int amount, TransactionType transactionType) {
		this.maxDays = maxDays;
		this.minSessions = minSessions;
		this.amount = amount;
		this.transactionType = transactionType;
	}
}

