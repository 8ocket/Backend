package com.kt.mindLog.dto.credit;

import java.time.LocalDateTime;
import java.util.List;

import com.kt.mindLog.domain.credit.TransactionType;

public record UserCreditResponse(
	int totalCredit,
	List<CreditTransaction> transactions
) {
	public static UserCreditResponse of(int totalCredit, List<CreditTransaction> transactions) {
		return new UserCreditResponse(totalCredit, transactions);
	}

	public record CreditTransaction(
		TransactionType transactionType,
		int amount,
		LocalDateTime createdAt
	) {
		public static CreditTransaction of(TransactionType transactionType, int amount, LocalDateTime createdAt) {
			return new CreditTransaction(transactionType, amount, createdAt);
		}
	}
}
